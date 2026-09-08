package com.example.aiandroidagent.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aiandroidagent.data.AutomationRuleEntity
import com.example.aiandroidagent.data.TaskEntity

// Clean modern Gemini-inspired color palette definitions
val GeminiBlue = Color(0xFF1A73E8)
val GeminiPurple = Color(0xFF8AB4F8)
val GeminiDarkBg = Color(0xFF121214)
val GeminiDarkSurface = Color(0xFF1E1F22)
val StatusGreen = Color(0xFF34A853)
val StatusAmber = Color(0xFFFBBC05)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupWizardScreen(
    accessibilityGranted: Boolean,
    microphoneGranted: Boolean,
    notificationsGranted: Boolean,
    smsGranted: Boolean,
    onEnableAccessibility: () -> Unit,
    onRequestMicrophone: () -> Unit,
    onRequestNotifications: () -> Unit,
    onRequestSms: () -> Unit,
    onRecheckAll: () -> Unit,
    onContinue: () -> Unit
) {
    val allGranted = accessibilityGranted && microphoneGranted && notificationsGranted && smsGranted

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GeminiDarkBg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GeminiBlue, GeminiPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("AI", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Welcome to AI Agent",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                "Set up required permissions to enable your offline assistant.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
        }

        // Accessibility card
        item {
            PermissionCard(
                title = "Accessibility Service",
                description = "Allows the agent to observe screens, click UI texts, and perform requested workflows.",
                isGranted = accessibilityGranted,
                onEnable = onEnableAccessibility,
                buttonText = "Enable Service"
            )
        }

        // Microphone card
        item {
            PermissionCard(
                title = "Microphone Permission",
                description = "Enables real-time voice command input and transcription.",
                isGranted = microphoneGranted,
                onEnable = onRequestMicrophone,
                buttonText = "Grant Access"
            )
        }

        // Notifications card
        item {
            PermissionCard(
                title = "Notification Delivery",
                description = "Shows persistent status notifications and controls for active background operations.",
                isGranted = notificationsGranted,
                onEnable = onRequestNotifications,
                buttonText = "Allow Notifications"
            )
        }

        // SMS card
        item {
            PermissionCard(
                title = "SMS & Event Automation",
                description = "Allows intercepting specified contact messages to process automated responses.",
                isGranted = smsGranted,
                onEnable = onRequestSms,
                buttonText = "Allow SMS"
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onRecheckAll,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    Text("Re-check State")
                }
                
                Button(
                    onClick = onContinue,
                    enabled = allGranted,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (allGranted) GeminiBlue else Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text("Continue Setup")
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun PermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onEnable: () -> Unit,
    buttonText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GeminiDarkSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    shape = CircleShape,
                    color = (if (isGranted) StatusGreen else StatusAmber).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isGranted) StatusGreen else StatusAmber)
                        )
                        Text(
                            text = if (isGranted) "Granted" else "Required",
                            color = if (isGranted) StatusGreen else StatusAmber,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = description,
                color = Color.LightGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            if (!isGranted) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onEnable,
                    colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(buttonText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAgentScreen(
    agentModeOn: Boolean,
    onToggleAgentMode: (Boolean) -> Unit,
    currentTaskState: String, // "Active" / "Paused"
    commandInput: String,
    onCommandChange: (String) -> Unit,
    onSendCommand: () -> Unit,
    onVoiceInputClick: () -> Unit,
    isListeningVoice: Boolean,
    conversationLog: List<String>,
    automationRules: List<AutomationRuleEntity>,
    onAddRule: (trigger: String, contact: String, reply: String) -> Unit,
    onDeleteRule: (AutomationRuleEntity) -> Unit,
    historyLog: List<TaskEntity>,
    onClearHistory: () -> Unit,
    onResetSetup: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Console", "Rules", "History")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(colors = listOf(GeminiBlue, GeminiPurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("AI Android Agent", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    IconButton(onClick = onResetSetup) {
                        Icon(Icons.Default.Settings, "Setup Setup", tint = Color.LightGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GeminiDarkBg)
            )
        },
        containerColor = GeminiDarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Persistent Status & ON/OFF Toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GeminiDarkSurface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (agentModeOn) StatusGreen else Color.Gray)
                            )
                            Text(
                                text = if (agentModeOn) "● Agent Mode ON" else "○ Agent Paused",
                                color = if (agentModeOn) StatusGreen else Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (agentModeOn) "Monitoring system events & rules" else "Toggle ON to active workflows",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                    
                    Switch(
                        checked = agentModeOn,
                        onCheckedChange = onToggleAgentMode,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = GeminiBlue,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }
            }

            // Tab bar
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GeminiDarkBg,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GeminiBlue
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                        selectedContentColor = GeminiBlue,
                        unselectedContentColor = Color.Gray
                    )
                }
            }

            // Tab contents
            Box(modifier = Modifier.weight(1.0f)) {
                when (selectedTab) {
                    0 -> ConsoleTab(
                        agentModeOn = agentModeOn,
                        commandInput = commandInput,
                        onCommandChange = onCommandChange,
                        onSendCommand = onSendCommand,
                        onVoiceInputClick = onVoiceInputClick,
                        isListeningVoice = isListeningVoice,
                        conversationLog = conversationLog
                    )
                    1 -> RulesTab(
                        automationRules = automationRules,
                        onAddRule = onAddRule,
                        onDeleteRule = onDeleteRule
                    )
                    2 -> HistoryTab(
                        historyLog = historyLog,
                        onClearHistory = onClearHistory
                    )
                }
            }
        }
    }
}

@Composable
fun ConsoleTab(
    agentModeOn: Boolean,
    commandInput: String,
    onCommandChange: (String) -> Unit,
    onSendCommand: () -> Unit,
    onVoiceInputClick: () -> Unit,
    isListeningVoice: Boolean,
    conversationLog: List<String>
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GeminiDarkSurface)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (conversationLog.isEmpty()) {
                    item {
                        Text(
                            text = "No active conversations. Type or speak a command like \"Open CapCut\" or \"Select video\" to begin.",
                            color = Color.Gray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp)
                        )
                    }
                } else {
                    items(conversationLog) { log ->
                        val isUser = log.startsWith("You:")
                        val content = if (isUser) log.substringAfter("You:").trim() else log.substringAfter("Agent:").trim()
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 16.dp
                                ),
                                color = if (isUser) GeminiBlue else Color.DarkGray
                            ) {
                                Text(
                                    text = content,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice Equalizer pulse animation simulation
        if (isListeningVoice) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GeminiBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Listening to Bengali speech...",
                    color = GeminiPurple,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = commandInput,
                onValueChange = onCommandChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Command (e.g. Open CapCut)", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = GeminiBlue,
                    unfocusedBorderColor = Color.DarkGray,
                    containerColor = GeminiDarkSurface
                ),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(onClick = onVoiceInputClick) {
                        Icon(
                            imageVector = if (isListeningVoice) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Voice input",
                            tint = if (isListeningVoice) Color.Red else Color.White
                        )
                    }
                }
            )

            FloatingActionButton(
                onClick = onSendCommand,
                containerColor = GeminiBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Send, "Send Command")
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RulesTab(
    automationRules: List<AutomationRuleEntity>,
    onAddRule: (trigger: String, contact: String, reply: String) -> Unit,
    onDeleteRule: (AutomationRuleEntity) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var inputContact by remember { mutableStateOf("") }
    var inputReply by remember { mutableStateOf("আমি এখন ব্যস্ত, পরে কথা বলব।") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Automated Replies", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Button(
                onClick = { showDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue)
            ) {
                Icon(Icons.Default.Add, "Add")
                Spacer(Modifier.width(4.dp))
                Text("Add Rule")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (automationRules.isEmpty()) {
                item {
                    Text(
                        "No custom rules created yet. Add a rule to auto-reply to messages.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    )
                }
            } else {
                items(automationRules) { rule ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GeminiDarkSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "WHEN: SMS from \"${rule.senderPattern.ifEmpty { "Anyone" }}\"",
                                    color = GeminiPurple,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "REPLY: ${rule.actionText}",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            IconButton(onClick = { onDeleteRule(rule) }) {
                                Icon(Icons.Default.Delete, "Delete Rule", tint = Color.Red.copy(alpha = 0.8f))
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("New Automation Rule", color = Color.White) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Trigger: Incoming SMS", color = Color.LightGray, fontSize = 14.sp)
                        OutlinedTextField(
                            value = inputContact,
                            onValueChange = { inputContact = it },
                            label = { Text("Contact Number / Pattern (empty for all)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GeminiBlue,
                                containerColor = GeminiDarkBg
                            )
                        )
                        OutlinedTextField(
                            value = inputReply,
                            onValueChange = { inputReply = it },
                            label = { Text("Reply Message (Bengali supported)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = GeminiBlue,
                                containerColor = GeminiDarkBg
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAddRule("SMS_RECEIVED", inputContact, inputReply)
                            showDialog = false
                            inputContact = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GeminiBlue)
                    ) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel", color = Color.LightGray)
                    }
                },
                containerColor = GeminiDarkSurface
            )
        }
    }
}

@Composable
fun HistoryTab(
    historyLog: List<TaskEntity>,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Logged Executions", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (historyLog.isNotEmpty()) {
                TextButton(onClick = onClearHistory) {
                    Icon(Icons.Default.Delete, "Clear", tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Clear All", color = Color.Red)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (historyLog.isEmpty()) {
                item {
                    Text(
                        "No history logs recorded yet.",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    )
                }
            } else {
                items(historyLog) { task ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GeminiDarkSurface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.command,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ID: ${task.id}",
                                    color = Color.Gray,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = task.status,
                                color = GeminiPurple,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
