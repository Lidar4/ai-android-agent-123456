package com.example.aiandroidagent.security

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

data class PermissionStatus(val microphone:Boolean,val accessibility:Boolean,val screenCapture:Boolean)
class PermissionManager(private val context:Context){ fun status(screenCapture:Boolean)=PermissionStatus(ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED,com.example.aiandroidagent.accessibility.AIAccessibilityService.connected,screenCapture) }
