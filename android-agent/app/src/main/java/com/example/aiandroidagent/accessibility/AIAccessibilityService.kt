package com.example.aiandroidagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.aiandroidagent.agent.AgentOrchestrator
import com.example.aiandroidagent.agent.DeviceState
import com.example.aiandroidagent.ai.GeminiProvider
import com.example.aiandroidagent.automation.DeviceActionExecutor
import com.example.aiandroidagent.security.ActionPolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AIAccessibilityService : AccessibilityService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var agentJob: Job? = null
    private lateinit var orchestrator: AgentOrchestrator

    companion object {
        private var instance: AIAccessibilityService? = null
        private val _snapshot = MutableStateFlow<List<String>>(emptyList())
        private val _events = MutableSharedFlow<String>(extraBufferCapacity = 64)
        private val _commands = MutableSharedFlow<String>(extraBufferCapacity = 16)

        val snapshot = _snapshot.asStateFlow()
        val events = _events.asSharedFlow()
        val connected: Boolean get() = instance != null

        const val PREFS = "agent_settings"
        const val KEY_ENABLED = "agent_enabled"

        fun submitCommand(command: String): Boolean = command.isNotBlank() && _commands.tryEmit(command.trim())
        fun stopAgent() { instance?.stopAgentInternal() }
        fun clickText(text: String): Boolean = instance?.findText(text)?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
        fun clickDescription(description: String): Boolean = instance?.findDescription(description)?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
        fun back(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_BACK) == true
        fun home(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_HOME) == true
        fun recents(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_RECENTS) == true

        fun clickCoordinate(x: Float, y: Float): Boolean {
            val service = instance ?: return false
            val path = Path().apply { moveTo(x, y) }
            return service.dispatchGesture(
                GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(path, 0, 80)).build(), null, null
            )
        }

        fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long): Boolean {
            val service = instance ?: return false
            val safeDuration = duration.coerceIn(50L, 5000L)
            val path = Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
            return service.dispatchGesture(
                GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(path, 0, safeDuration)).build(), null, null
            )
        }

        fun scroll(forward: Boolean): Boolean {
            val root = instance?.rootInActiveWindow ?: return false
            val scrollable = findFirst(root) { node ->
                node.isScrollable && node.actionList.any { action ->
                    action.id == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD || action.id == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                }
            } ?: return false
            return scrollable.performAction(if (forward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
        }

        fun typeText(text: String): Boolean {
            val root = instance?.rootInActiveWindow ?: return false
            val focused = findFirst(root) { it.isFocused && it.isEditable } ?: findFirst(root) { it.isEditable } ?: return false
            val args = Bundle().apply { putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text) }
            return focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }

        fun currentTexts(): List<String> = _snapshot.value

        private fun findFirst(node: AccessibilityNodeInfo, predicate: (AccessibilityNodeInfo) -> Boolean): AccessibilityNodeInfo? {
            if (predicate(node)) return node
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                findFirst(child, predicate)?.let { return it }
            }
            return null
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        val gemini = GeminiProvider()
        orchestrator = AgentOrchestrator(gemini, ActionPolicy()) { action -> DeviceActionExecutor(this, gemini).execute(action) }
        serviceScope.launch {
            _commands.collect { command ->
                if (!getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_ENABLED, false)) {
                    _events.tryEmit("Agent is OFF; command ignored")
                } else {
                    agentJob?.cancel()
                    agentJob = launch {
                        orchestrator.run(command, { DeviceState(accessibilityNodes = _snapshot.value) }, { _events.tryEmit(it) }, false)
                    }
                }
            }
        }
        _events.tryEmit("Accessibility agent connected")
    }

    private fun stopAgentInternal() {
        agentJob?.cancel()
        _events.tryEmit("Agent stopped")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        _snapshot.value = read(root).take(160)
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        agentJob?.cancel()
        serviceScope.cancel()
        instance = null
        _snapshot.value = emptyList()
        super.onDestroy()
    }

    private fun findText(text: String): AccessibilityNodeInfo? = rootInActiveWindow?.let { root -> findFirst(root) { it.text?.toString()?.equals(text, ignoreCase = true) == true } }
    private fun findDescription(text: String): AccessibilityNodeInfo? = rootInActiveWindow?.let { root -> findFirst(root) { it.contentDescription?.toString()?.equals(text, ignoreCase = true) == true } }

    private fun read(node: AccessibilityNodeInfo): List<String> = buildList {
        node.text?.toString()?.takeIf { it.isNotBlank() }?.let(::add)
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let(::add)
        for (i in 0 until node.childCount) node.getChild(i)?.let { addAll(read(it)) }
    }
}
