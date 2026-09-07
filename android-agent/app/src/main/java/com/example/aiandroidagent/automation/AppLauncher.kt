package com.example.aiandroidagent.automation

import android.content.Context
import android.content.Intent

class AppLauncher(private val context:Context){ fun launch(packageName:String):Result<Unit> = runCatching { val intent=context.packageManager.getLaunchIntentForPackage(packageName) ?: error("App not installed: $packageName"); intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);context.startActivity(intent) } }
