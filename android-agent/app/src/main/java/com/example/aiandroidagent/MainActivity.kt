package com.example.aiandroidagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.aiandroidagent.agent.AgentAction
import com.example.aiandroidagent.agent.DeviceState
import com.example.aiandroidagent.ai.GeminiProvider
import com.example.aiandroidagent.automation.AppLauncher
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
        val scope = rememberCoroutineScope()
        val provider = remember { GeminiProvider() }

        MaterialTheme {
            Scaffold(topBar = { TopAppBar(title = { Text("AI Android Agent") }) }) { padding ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Agent Console", style = MaterialTheme.typography.titleLarge)
                            Text("Plan commands locally, show the generated action plan, and safely launch supported apps.")
                            Text("Status: $status")
                        }
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
                            status = "Planning…"
                            goal = ""
                            actions = emptyList()
                            scope.launch {
                                val result = provider.createPlan(input, DeviceState())
                                result.onSuccess { plan ->
                                    goal = plan.goal
                                    actions = plan.actions.map(::actionLabel)
                                    status = "Plan ready (${plan.actions.size} action${if (plan.actions.size == 1) "" else "s"})"

                                    val first = plan.actions.firstOrNull()
                                    if (first is AgentAction.OpenApp) {
                                        AppLauncher(this@MainActivity).launch(first.packageName)
                                            .onSuccess { status = "Opened ${first.packageName}" }
                                            .onFailure { status = "Could not open app: ${it.message ?: "unknown error"}" }
                                    }
                                }.onFailure {
                                    status = "Error: ${it.message ?: "Unable to create plan"}"
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
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(actions) { Text("• $it") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun actionLabel(action: AgentAction): String = when (action) {
        is AgentAction.OpenApp -> "Open app: ${action.packageName}"
        is AgentAction.Wait -> "Wait ${action.milliseconds} ms"
        is AgentAction.Observe -> "Observe current screen"
        is AgentAction.ClickText -> "Click text: ${action.text}"
        is AgentAction.ClickDescription -> "Click description: ${action.description}"
        is AgentAction.AskUserConfirmation -> "Ask confirmation: ${action.prompt}"
        else -> action::class.simpleName ?: "Action"
    }
}
