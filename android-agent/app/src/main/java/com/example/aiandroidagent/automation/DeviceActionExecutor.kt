package com.example.aiandroidagent.automation

import android.content.Context
import com.example.aiandroidagent.accessibility.AIAccessibilityService
import com.example.aiandroidagent.agent.*
import kotlinx.coroutines.delay

class DeviceActionExecutor(private val context: Context, private val aiClient: com.example.aiandroidagent.ai.AIClient) {
    private val launcher = AppLauncher(context)

    suspend fun execute(action: AgentAction): ActionResult = try {
        when (action) {
            is AgentAction.OpenApp -> launcher.launch(action.packageName).fold(
                { ActionResult(true, "Opened ${action.packageName}") },
                { ActionResult(false, it.message ?: "Launch failed", "APP_LAUNCH") }
            )
            is AgentAction.ClickText -> result(
                AIAccessibilityService.clickText(action.text),
                "Clicked text ${action.text}",
                "CLICK_TEXT"
            )
            is AgentAction.ClickDescription -> result(
                AIAccessibilityService.clickDescription(action.description),
                "Clicked description ${action.description}",
                "CLICK_DESCRIPTION"
            )
            is AgentAction.ClickCoordinate -> result(
                AIAccessibilityService.clickCoordinate(action.x, action.y),
                "Clicked (${action.x}, ${action.y})",
                "CLICK_COORDINATE"
            )
            is AgentAction.Swipe -> result(
                AIAccessibilityService.swipe(action.x1, action.y1, action.x2, action.y2, action.durationMs),
                "Swiped",
                "SWIPE"
            )
            is AgentAction.Scroll -> result(
                AIAccessibilityService.scroll(action.forward),
                if (action.forward) "Scrolled forward" else "Scrolled backward",
                "SCROLL"
            )
            is AgentAction.TypeText -> result(
                AIAccessibilityService.typeText(action.text),
                "Typed text",
                "TYPE_TEXT"
            )
            is AgentAction.Back -> result(AIAccessibilityService.back(), "Back", "BACK")
            is AgentAction.Home -> result(AIAccessibilityService.home(), "Home", "HOME")
            is AgentAction.Recents -> result(AIAccessibilityService.recents(), "Recents", "RECENTS")
            is AgentAction.Wait -> {
                delay(action.milliseconds.coerceIn(0L, 30_000L))
                ActionResult(true, "Waited")
            }
            is AgentAction.Observe -> ActionResult(
                AIAccessibilityService.connected,
                "Accessibility snapshot available: ${AIAccessibilityService.currentTexts().size} items",
                if (AIAccessibilityService.connected) null else "ACCESSIBILITY_NOT_CONNECTED"
            )
            is AgentAction.ScreenAnalyze -> {
                val imageBytes = com.example.aiandroidagent.vision.ScreenCaptureHelper.lastCapturedImage ?: "simulate_screenshot".toByteArray()
                aiClient.analyzeScreen(imageBytes).fold(
                    { ActionResult(true, "Screen analysis: $it") },
                    { ActionResult(false, it.message ?: "Screen analysis failed", "VISION_FAILED") }
                )
            }
            is AgentAction.AskUserConfirmation -> ActionResult(
                false,
                action.prompt,
                "USER_CONFIRMATION_REQUIRED"
            )
            is AgentAction.Stop -> ActionResult(true, "Stopped")
        }
    } catch (e: Exception) {
        ActionResult(false, e.message ?: "Action failed", "EXECUTION")
    }

    private fun result(success: Boolean, message: String, errorCode: String): ActionResult =
        ActionResult(success, message, if (success) null else errorCode)
}
