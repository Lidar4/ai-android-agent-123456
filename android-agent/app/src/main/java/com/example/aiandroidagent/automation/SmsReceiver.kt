package com.example.aiandroidagent.automation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import com.example.aiandroidagent.data.DatabaseProvider
import com.example.aiandroidagent.data.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras ?: return
            val pdus = bundle.get("pdus") as? Array<*> ?: return
            val format = bundle.getString("format")
            
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = DatabaseProvider.create(context)
                    val repository = TaskRepository(db)
                    val rules = repository.getRules()
                    
                    for (pdu in pdus) {
                        val bytes = pdu as? ByteArray ?: continue
                        val message = if (android.os.Build.VERSION.SDK_INT >= 23) {
                            SmsMessage.createFromPdu(bytes, format)
                        } else {
                            @Suppress("DEPRECATION")
                            SmsMessage.createFromPdu(bytes)
                        } ?: continue
                        
                        val sender = message.originatingAddress ?: ""
                        val body = message.messageBody ?: ""

                        for (rule in rules) {
                            if (rule.isEnabled && rule.triggerType == "SMS_RECEIVED") {
                                val match = rule.senderPattern.isEmpty() || 
                                            sender.contains(rule.senderPattern, ignoreCase = true)
                                if (match) {
                                    // Record the event in Room
                                    repository.save("SMS Event Match from $sender", "Triggered auto-reply")
                                    repository.addConversation("user", "Incoming SMS from $sender: \"$body\"")
                                    repository.addConversation("agent", "Auto-reply: \"${rule.actionText}\"")
                                    
                                    // Optionally try sending the reply message
                                    try {
                                        val smsManager = if (android.os.Build.VERSION.SDK_INT >= 31) {
                                            context.getSystemService(android.telephony.SmsManager::class.java)
                                        } else {
                                            @Suppress("DEPRECATION")
                                            android.telephony.SmsManager.getDefault()
                                        }
                                        smsManager.sendTextMessage(sender, null, rule.actionText, null, null)
                                    } catch (e: Exception) {
                                        // Ignore permission/sending failure in sandbox
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
