package com.example.aiandroidagent

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiandroidagent.accessibility.AIAccessibilityService
import com.example.aiandroidagent.agent.AgentAction
import com.example.aiandroidagent.agent.AgentOrchestrator
import com.example.aiandroidagent.agent.DeviceState
import com.example.aiandroidagent.ai.GeminiProvider
import com.example.aiandroidagent.automation.AppLauncher
import com.example.aiandroidagent.automation.DeviceActionExecutor
import com.example.aiandroidagent.security.ActionPolicy
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AgentApp() }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AgentApp() {
        var command by remember { mutableStateOf("") }
        var status by remember { mutableStateOf("Ready") }
        var goal by remember { mutableStateOf("") }
        var actions by remember { mutableStateOf<List<String>>(emptyList()) }
        var running by remember { mutableStateOf(false) }
        val events = remember { mutableStateListOf<String>() }
        val scope = rememberCoroutineScope()
        val provider = remember { GeminiProvider() }
        val orchestrator = remember {
            AgentOrchestrator(
                provider,
                ActionPolicy(),
                DeviceActionExecutor(this@MainActivity, provider)::execute
            )
        }

        LaunchedEffect(Unit) {
            AIAccessibilityService.events.collect { event ->
                events.add(event)
                if (events.size > 80) events.removeAt(0)
                status = event
            }
        }

        MaterialTheme {
            Scaffold(topBar = { TopAppBar(title = { Text("AI Android Agent") }) }) { padding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Real Device Agent", style = MaterialTheme.typography.titleLarge)
                            Text(if (AIAccessibilityService.connected) "Automation: CONNECTED" else "Automation: NOT ENABLED")
                            Text("Status: $status")
                        }
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Enable Automation") }
                        OutlinedButton(
                            onClick = {
                                orchestrator.cancel()
                                AIAccessibilityService.stopAgent()
                                running = false
                                status = "Stopped"
                            },
                            enabled = running,
                            modifier = Modifier.weight(1f)
                        ) { Text("Stop") }
                    }

                    OutlinedTextField(
                        value = command,
                        onValueChange = { command = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Command") },
                        placeholder = { Text("Try: open YouTube") },
                        enabled = !running
                    )

                    Button(
                        enabled = command.isNotBlank() && !running,
                        onClick = {
                            val input = command.trim()
                            running = true
                            status = "Planning..."
                            goal = ""
                            actions = emptyList()
                            events.clear()

                            scope.launch {
                                val planResult = provider.createPlan(input, DeviceState(accessibilityNodes = AIAccessibilityService.currentTexts()))
                                planResult.onSuccess { plan ->
                                    goal = plan.goal
                                    actions = plan.actions.map(::actionLabel)
                                    events.add("Plan ready: ${plan.actions.size} action(s)")

                                    val needsAccessibility = plan.actions.any {
                                        it !is AgentAction.OpenApp && it !is AgentAction.Wait && it !is AgentAction.Observe && it !is AgentAction.Stop
                                    }

                                    if (!AIAccessibilityService.connected && needsAccessibility) {
                                        status = "Enable Automation first for this task"
                                        events.add("This command needs Accessibility Automation. Tap Enable Automation, enable the service, then Run again.")
                                    } else if (!AIAccessibilityService.connected) {
                                        runSimplePlan(plan.actions, events) { message -> status = message }
                                    } else {
                                        status = "Executing..."
                                        orchestrator.run(
                                            input,
                                            { DeviceState(accessibilityNodes = AIAccessibilityService.currentTexts()) },
                                            { message ->
                                                events.add(message)
                                                if (events.size > 80) events.removeAt(0)
                                                status = message
                                            },
                                            simulate = false
                                        )
                                    }
                                }.onFailure {
                                    status = "Error: ${it.message ?: "Unable to create plan"}"
                                    events.add(status)
                                }
                                running = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (running) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Run")
                    }

                    if (goal.isNotBlank()) {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Goal", style = MaterialTheme.typography.titleMedium)
                                Text(goal)
                                Text("Action plan", style = MaterialTheme.typography.titleMedium)
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                                    items(actions) { Text("• $it") }
                                }
                            }
                        }
                    }

                    if (events.isNotEmpty()) {
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Live execution log", style = MaterialTheme.typography.titleMedium)
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.fillMaxWidth()) {
                                    items(events) { Text(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun runSimplePlan(
        actions: List<AgentAction>,
        events: MutableList<String>,
        onStatus: (String) -> Unit
    ) {
        for (action in actions) {
            when (action) {
                is AgentAction.OpenApp -> {
                    events.add("Opening ${action.packageName}...")
                    AppLauncher(this).launch(action.packageName)
                        .onSuccess { onStatus("Opened ${action.packageName}") }
                        .onFailure { onStatus("Could not open ${action.packageName}: ${it.message ?: "not installed"}") }
                }
                is AgentAction.Wait -> kotlinx.coroutines.delay(action.milliseconds.coerceIn(0L, 30_000L))
                is AgentAction.Observe -> events.add("Observation skipped: Automation service is not enabled")
                is AgentAction.Stop -> return
                else -> return
            }
        }
        onStatus("Task complete")
        events.add("Task complete")
    }

    private fun actionLabel(action: AgentAction): String = when (action) {
        is AgentAction.OpenApp -> "Open app: ${action.packageName}"
        is AgentAction.Wait -> "Wait ${action.milliseconds} ms"
        is AgentAction.Observe -> "Observe current screen"
        is AgentAction.ClickText -> "Click text: ${action.text}"
        is AgentAction.ClickDescription -> "Click description: ${action.description}"
        is AgentAction.ClickCoordinate -> "Click coordinate: ${action.x}, ${action.y}"
        is AgentAction.Swipe -> "Swipe ${action.x1},${action.y1} → ${action.x2},${action.y2}"
        is AgentAction.Scroll -> "Scroll ${if (action.forward) "forward" else "backward"}"
        is AgentAction.TypeText -> "Type text: ${action.text}"
        is AgentAction.Back -> "Press Back"
        is AgentAction.Home -> "Press Home"
        is AgentAction.Recents -> "Open Recents"
        is AgentAction.ScreenAnalyze -> "Analyze screen"
        is AgentAction.AskUserConfirmation -> "Ask confirmation: ${action.prompt}"
        is AgentAction.Stop -> "Stop"
    }
}
