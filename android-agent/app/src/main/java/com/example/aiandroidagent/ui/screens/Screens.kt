package com.example.aiandroidagent.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable fun HomeScreen(onStart:()->Unit,onPermissions:()->Unit){Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){Text("AI Android Agent",style=MaterialTheme.typography.headlineMedium);Text("Agent Ready");Button(onClick=onStart){Text("Start Agent")};OutlinedButton(onClick=onPermissions){Text("Permission setup")}}}
@Composable fun AgentScreen(log:List<String>){LazyColumn{items(log.size){Text(log[it],Modifier.padding(8.dp))}}}
@Composable fun PermissionsScreen(){Text("Enable Accessibility, microphone, and screen capture from Android settings.",Modifier.padding(16.dp))}
@Composable fun SettingsScreen(){Text("Settings",Modifier.padding(16.dp))}
@Composable fun TaskHistoryScreen(){Text("Task history is stored locally with Room.",Modifier.padding(16.dp))}
@Composable fun DebugScreen(state:String){Text(state,Modifier.padding(16.dp))}
