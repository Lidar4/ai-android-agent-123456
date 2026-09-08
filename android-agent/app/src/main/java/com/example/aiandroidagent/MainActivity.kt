package com.example.aiandroidagent

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.aiandroidagent.accessibility.AIAccessibilityService
import com.example.aiandroidagent.agent.ActionResult
import com.example.aiandroidagent.agent.AgentAction
import com.example.aiandroidagent.agent.AgentOrchestrator
import com.example.aiandroidagent.agent.DeviceState
import com.example.aiandroidagent.ai.GeminiProvider
import com.example.aiandroidagent.automation.AppLauncher
import com.example.aiandroidagent.security.ActionPolicy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val microphonePermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AgentApp(
                microphoneGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED,
                onRequestMicrophone = { microphonePermission.launch(Manifest.permission.RECORD_AUDIO) },
                onOpenAccessibility = { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                onOpenProjectionHelp = { startActivity(Intent(Settings.ACTION_SETTINGS)) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentApp(
    microphoneGranted: Boolean,
    onRequestMicrophone: () -> Unit,
    onOpenAccessibility: () -> Unit,
    onOpenProjectionHelp: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var command by remember { mutableStateOf("") }
    var running by remember { mutableStateOf(false) }
    var simulation by remember { mutableStateOf(true) }
    val log = remember { mutableStateListOf("Agent initialized", "Waiting for a command") }
    val orchestrator = remember {
        AgentOrchestrator(GeminiProvider(), ActionPolicy()) { action ->
            when (action) {
                is AgentAction.OpenApp -> ActionResult(AppLauncher(context).launch(action.packageName).isSuccess, "Opened ${action.packageName}")
                is AgentAction.Back -> ActionResult(AIAccessibilityService.back(), "Back action")
                is AgentAction.Home -> ActionResult(AIAccessibilityService.home(), "Home action")
                is AgentAction.Recents -> ActionResult(AIAccessibilityService.recents(), "Recents action")
                is AgentAction.ClickText -> ActionResult(AIAccessibilityService.clickText(action.text), "Clicked ${action.text}")
                is AgentAction.ClickDescription -> ActionResult(AIAccessibilityService.clickDescription(action.description), "Clicked ${action.description}")
                is AgentAction.Swipe -> ActionResult(AIAccessibilityService.swipe(action.x1, action.y1, action.x2, action.y2, action.durationMs), "Swipe completed")
                is AgentAction.Wait -> { delay(action.milliseconds); ActionResult(true, "Waited ${action.milliseconds} ms") }
                else -> ActionResult(true, "Observed or simulated ${action::class.simpleName}")
            }
        }
    }

    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("AI Android Agent") }) }) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    StatusCard(
                        title = if (running) "Agent active" else "Agent ready",
                        values = listOf(
                            "Accessibility" to if (AIAccessibilityService.connected) "Connected" else "Required",
                            "Microphone" to if (microphoneGranted) "Granted" else "Required",
                            "Screen vision" to "Consent required",
                            "AI planner" to "Offline-safe"
                        )
                    )
                }
                item {
                    OutlinedTextField(command, { command = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Type a command") })
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = onRequestMicrophone) { Text("Microphone") }
                        OutlinedButton(onClick = onOpenAccessibility) { Text("Accessibility") }
                    }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onOpenProjectionHelp) { Text("Screen capture settings") }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(selected = simulation, onClick = { simulation = !simulation }, label = { Text("Simulation mode") })
                        Button(enabled = command.isNotBlank() && !running, onClick = {
                            running = true
                            log.add("Starting: $command")
                            scope.launch {
                                orchestrator.run(command, { DeviceState(accessibilityNodes = AIAccessibilityService.snapshot.value) }, { event -> log.add(event) }, simulation)
                                running = false
                            }
                        }) { Text("Start agent") }
                        OutlinedButton(onClick = { orchestrator.cancel(); running = false; log.add("Emergency stop") }) { Text("Stop") }
                    }
                }
                item { Text("Activity log", style = MaterialTheme.typography.titleMedium) }
                items(log) { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}

@Composable
private fun StatusCard(title: String, values: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            values.forEach { (label, value) -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text(value) } }
        }
    }
}
