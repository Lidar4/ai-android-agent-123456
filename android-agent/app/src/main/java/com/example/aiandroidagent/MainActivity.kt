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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.aiandroidagent.accessibility.AIAccessibilityService

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
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(AIAccessibilityService.PREFS, 0) }
    var enabled by remember { mutableStateOf(prefs.getBoolean(AIAccessibilityService.KEY_ENABLED, false)) }
    var command by remember { mutableStateOf("") }
    val log = remember { mutableStateListOf("Agent initialized") }

    LaunchedEffect(Unit) {
        AIAccessibilityService.events.collect { event: String ->
            log.add(event)
            if (log.size > 80) log.removeAt(0)
        }
    }

    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("AI Android Agent") }) }) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(if (enabled) "Agent is ON" else "Agent is OFF", style = MaterialTheme.typography.titleLarge)
                                    Text(if (enabled) "Works while you use other apps" else "Turn it on after setup")
                                }
                                Switch(
                                    checked = enabled,
                                    onCheckedChange = { value ->
                                        enabled = value
                                        prefs.edit().putBoolean(AIAccessibilityService.KEY_ENABLED, value).apply()
                                        if (!value) AIAccessibilityService.stopAgent()
                                    }
                                )
                            }
                        }
                    }
                }
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Setup", style = MaterialTheme.typography.titleMedium)
                            Text("Accessibility: ${if (AIAccessibilityService.connected) "Ready" else "Enable in Android Settings"}")
                            Text("Microphone: ${if (microphoneGranted) "Ready" else "Optional"}")
                            Text("Screen vision: User consent required when needed")
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = onOpenAccessibility) { Text("Accessibility") }
                                OutlinedButton(onClick = onRequestMicrophone) { Text("Microphone") }
                            }
                            OutlinedButton(onClick = onOpenProjectionHelp) { Text("Screen capture settings") }
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = command,
                        onValueChange = { command = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Tell the agent what to do") },
                        placeholder = { Text("Example: Open CapCut") }
                    )
                }
                item {
                    Button(
                        enabled = enabled && AIAccessibilityService.connected && command.isNotBlank(),
                        onClick = {
                            log.add("You: $command")
                            if (!AIAccessibilityService.submitCommand(command)) log.add("Could not submit command")
                            command = ""
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Run command") }
                }
                item { Text("Activity", style = MaterialTheme.typography.titleMedium) }
                items(log) { entry: String -> Text(entry, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
    }
}
