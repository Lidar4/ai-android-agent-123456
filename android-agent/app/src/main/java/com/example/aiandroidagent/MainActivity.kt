package com.example.aiandroidagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CleanAgentApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CleanAgentApp() {
    var command by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Ready") }

    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("AI Android Agent") }) }) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Core build", style = MaterialTheme.typography.titleLarge)
                        Text("Clean diagnostic build: network access only. Sensitive device-control capabilities are disabled.")
                        Text("Status: $status")
                    }
                }

                OutlinedTextField(
                    value = command,
                    onValueChange = { command = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Command") },
                    placeholder = { Text("Enter a command") }
                )

                Button(
                    enabled = command.isNotBlank(),
                    onClick = {
                        status = "Received: $command"
                        command = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Run")
                }
            }
        }
    }
}
