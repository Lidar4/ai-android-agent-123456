package com.example.aiandroidagent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.aiandroidagent.agent.*
import com.example.aiandroidagent.ai.GeminiProvider
import com.example.aiandroidagent.automation.AppLauncher
import com.example.aiandroidagent.security.ActionPolicy
import com.example.aiandroidagent.accessibility.AIAccessibilityService

class MainActivity:ComponentActivity(){ private val mic=registerForActivityResult(ActivityResultContracts.RequestPermission()){}; override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState); setContent{AgentApp(onMic={mic.launch(Manifest.permission.RECORD_AUDIO)},onAccessibility={startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))})}} }

@Composable fun AgentApp(onMic:()->Unit,onAccessibility:()->Unit){ val context=LocalContext.current; var command by remember{mutableStateOf("")}; var running by remember{mutableStateOf(false)}; var simulate by remember{mutableStateOf(true)}; val log= remember{ mutableStateListOf("Agent initialized", "Waiting for a command") }; val provider=remember{GeminiProvider()}; val orchestrator=remember{AgentOrchestrator(provider,ActionPolicy()){a->when(a){is AgentAction.OpenApp->ActionResult(AppLauncher(context).launch(a.packageName).isSuccess,"Opened ${a.packageName}");is AgentAction.Back->ActionResult(AIAccessibilityService.back(),"Back action");is AgentAction.Home->ActionResult(AIAccessibilityService.home(),"Home action");is AgentAction.Recents->ActionResult(AIAccessibilityService.recents(),"Recents action");is AgentAction.ClickText->ActionResult(AIAccessibilityService.clickText(a.text),"Click text ${a.text}");is AgentAction.ClickDescription->ActionResult(AIAccessibilityService.clickDescription(a.description),"Click description ${a.description}");is AgentAction.Swipe->ActionResult(AIAccessibilityService.swipe(a.x1,a.y1,a.x2,a.y2,a.durationMs),"Swipe");is AgentAction.Wait->{kotlinx.coroutines.delay(a.milliseconds);ActionResult(true,"Waited")};else->ActionResult(true,"Observed")}}})
 MaterialTheme(colorScheme=darkColorScheme(primary=Color(0xFF7EA7FF),background=Color(0xFF0D1117),surface=Color(0xFF151B24))){Scaffold(topBar={TopAppBar(title={Text("AI Android Agent")},actions={Text(if(running)"ACTIVE" else "READY",color=if(running)MaterialTheme.colorScheme.primary else Color(0xFF7DE2B2),modifier=Modifier.padding(end=16.dp))})}){pad->LazyColumn(modifier=Modifier.padding(pad).padding(16.dp),verticalArrangement=Arrangement.spacedBy(14.dp)){item{StatusCard("System readiness",listOf("Accessibility" to if(AIAccessibilityService.connected)"Connected" else "Not enabled","Screen vision" to "Consent required","Voice" to "Ready","AI planner" to "Offline-safe"))};item{OutlinedTextField(command,{command=it},modifier=Modifier.fillMaxWidth(),label={Text("Type a command")},placeholder={Text("Try: open YouTube")})};item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){Button(onClick={onMic()}){Text("Speak")};Button(onClick={onAccessibility}){Text("Permissions")}}};item{Row(horizontalArrangement=Arrangement.spacedBy(10.dp)){FilterChip(selected=simulate,onClick={simulate=!simulate},label={Text("Simulation mode")});Button(enabled=command.isNotBlank()&&!running,onClick={running=true;log.add("Starting: $command");orchestrator.run(command,{DeviceState(accessibilityNodes=AIAccessibilityService.snapshot.value)}, {log.add(it)},simulate);running=false}){Text("Start agent")};OutlinedButton(onClick={orchestrator.cancel();running=false;log.add("Emergency stop")}){Text("Stop")}}};item{Text("Activity log",style=MaterialTheme.typography.titleMedium)};items(log){Text(it,color=MaterialTheme.colorScheme.onSurfaceVariant,modifier=Modifier.padding(vertical=3.dp))}}}}
}
@Composable private fun StatusCard(title:String,items:List<Pair<String,String>>){Card(modifier=Modifier.fillMaxWidth()){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){Text(title,style=MaterialTheme.typography.titleMedium);items.forEach{(a,b)->Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){Text(a);Text(b,color=MaterialTheme.colorScheme.primary)}}}}}
