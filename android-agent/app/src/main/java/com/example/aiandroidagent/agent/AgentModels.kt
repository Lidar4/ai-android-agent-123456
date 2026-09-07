package com.example.aiandroidagent.agent

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable data class DeviceState(val timestamp: Long = System.currentTimeMillis(), val foregroundPackage: String? = null, val screenWidth: Int = 0, val screenHeight: Int = 0, val accessibilityNodes: List<String> = emptyList(), val screenshotAvailable: Boolean = false, val screenDescription: String? = null)
@Serializable data class AgentPlan(val goal: String, val actions: List<AgentAction>)
@Serializable sealed class AgentAction { abstract val id: String
 @Serializable @SerialName("OPEN_APP") data class OpenApp(val packageName: String, override val id: String = "open-${packageName}"): AgentAction()
 @Serializable @SerialName("CLICK_TEXT") data class ClickText(val text: String, override val id: String = "click-text-${text}"): AgentAction()
 @Serializable @SerialName("CLICK_DESCRIPTION") data class ClickDescription(val description: String, override val id: String = "click-desc-${description}"): AgentAction()
 @Serializable @SerialName("CLICK_COORDINATE") data class ClickCoordinate(val x: Float, val y: Float, override val id: String = "click-coordinate"): AgentAction()
 @Serializable @SerialName("SWIPE") data class Swipe(val x1: Float, val y1: Float, val x2: Float, val y2: Float, val durationMs: Long = 400, override val id: String = "swipe"): AgentAction()
 @Serializable @SerialName("SCROLL") data class Scroll(val forward: Boolean = true, override val id: String = "scroll"): AgentAction()
 @Serializable @SerialName("BACK") data class Back(override val id: String = "back"): AgentAction()
 @Serializable @SerialName("HOME") data class Home(override val id: String = "home"): AgentAction()
 @Serializable @SerialName("RECENTS") data class Recents(override val id: String = "recents"): AgentAction()
 @Serializable @SerialName("TYPE_TEXT") data class TypeText(val text: String, override val id: String = "type-text"): AgentAction()
 @Serializable @SerialName("WAIT") data class Wait(val milliseconds: Long, override val id: String = "wait"): AgentAction()
 @Serializable @SerialName("OBSERVE") data class Observe(override val id: String = "observe"): AgentAction()
 @Serializable @SerialName("SCREEN_ANALYZE") data class ScreenAnalyze(override val id: String = "screen-analyze"): AgentAction()
 @Serializable @SerialName("ASK_USER_CONFIRMATION") data class AskUserConfirmation(val prompt: String, override val id: String = "confirm"): AgentAction()
 @Serializable @SerialName("STOP") data class Stop(override val id: String = "stop"): AgentAction()
}
data class ActionResult(val success: Boolean, val message: String, val errorCode: String? = null, val timestamp: Long = System.currentTimeMillis())
enum class PolicyClass { SAFE, CONFIRMATION_REQUIRED, BLOCKED }
