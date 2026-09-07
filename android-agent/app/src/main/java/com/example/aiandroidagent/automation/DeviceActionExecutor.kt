package com.example.aiandroidagent.automation

import android.content.Context
import com.example.aiandroidagent.accessibility.AIAccessibilityService
import com.example.aiandroidagent.agent.*

class DeviceActionExecutor(context:Context){ private val launcher=AppLauncher(context); suspend fun execute(a:AgentAction):ActionResult=try{when(a){is AgentAction.OpenApp->launcher.launch(a.packageName).fold({ActionResult(true,"Opened ${a.packageName}")},{ActionResult(false,it.message?:"Launch failed","APP_LAUNCH")});is AgentAction.ClickText->ActionResult(AIAccessibilityService.clickText(a.text),"Clicked text ${a.text}");is AgentAction.ClickDescription->ActionResult(AIAccessibilityService.clickDescription(a.description),"Clicked description ${a.description}");is AgentAction.Swipe->ActionResult(AIAccessibilityService.swipe(a.x1,a.y1,a.x2,a.y2,a.durationMs),"Swiped");is AgentAction.Back->ActionResult(AIAccessibilityService.back(),"Back");is AgentAction.Home->ActionResult(AIAccessibilityService.home(),"Home");is AgentAction.Recents->ActionResult(AIAccessibilityService.recents(),"Recents");is AgentAction.Wait->{kotlinx.coroutines.delay(a.milliseconds);ActionResult(true,"Waited")};is AgentAction.Stop->ActionResult(true,"Stopped");else->ActionResult(true,"Observation requested")}}catch(e:Exception){ActionResult(false,e.message?:"Action failed","EXECUTION")}}
