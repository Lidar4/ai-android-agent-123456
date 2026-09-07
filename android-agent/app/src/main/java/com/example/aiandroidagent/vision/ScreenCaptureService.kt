package com.example.aiandroidagent.vision

import android.app.*
import android.content.Intent
import android.media.projection.MediaProjection
import android.os.IBinder

class ScreenCaptureService:Service(){ override fun onStartCommand(intent:Intent?,flags:Int,startId:Int):Int { if(android.os.Build.VERSION.SDK_INT>=26) startForeground(41,Notification.Builder(this,"screen-capture").setContentTitle("AI Android Agent").setContentText("Screen vision is active").setSmallIcon(android.R.drawable.ic_menu_view).build()); return START_NOT_STICKY }; override fun onBind(intent:Intent?):IBinder?=null }
class ScreenCaptureManager { var projection:MediaProjection?=null; var authorized=false; fun setProjection(value:MediaProjection){projection=value;authorized=true}; fun release(){projection?.stop();projection=null;authorized=false} }
