package com.example.aiandroidagent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun AIAndroidAgentTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF7EA7FF),
            secondary = Color(0xFF7DE2B2)
        ),
        content = content
    )
}
