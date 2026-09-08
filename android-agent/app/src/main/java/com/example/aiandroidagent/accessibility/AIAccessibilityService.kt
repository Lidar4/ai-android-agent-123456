package com.example.aiandroidagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIAccessibilityService : AccessibilityService() {
    companion object {
        private var instance: AIAccessibilityService? = null
        private val _snapshot = MutableStateFlow<List<String>>(emptyList())
        val snapshot = _snapshot.asStateFlow()
        val connected: Boolean get() = instance != null

        fun clickText(text: String): Boolean = instance?.findText(text)?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true

        fun clickDescription(description: String): Boolean = instance?.findDescription(description)?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true

        fun back(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_BACK) == true

        fun home(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_HOME) == true

        fun recents(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_RECENTS) == true

        fun clickCoordinate(x: Float, y: Float): Boolean {
            val service = instance ?: return false
            val path = Path().apply { moveTo(x, y) }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, 80))
                .build()
            return service.dispatchGesture(gesture, null, null)
        }

        fun swipe(x1: Float, y1: Float, x2: Float, y2: Float, duration: Long): Boolean {
            val service = instance ?: return false
            val safeDuration = duration.coerceIn(50L, 5000L)
            val path = Path().apply { moveTo(x1, y1); lineTo(x2, y2) }
            val gesture = GestureDescription.Builder()
                .addStroke(GestureDescription.StrokeDescription(path, 0, safeDuration))
                .build()
            return service.dispatchGesture(gesture, null, null)
        }

        fun scroll(forward: Boolean): Boolean {
            val root = instance?.rootInActiveWindow ?: return false
            val scrollable = findFirst(root) { node ->
                node.isScrollable && (node.actionList.any { action ->
                    action.id == AccessibilityNodeInfo.ACTION_SCROLL_FORWARD ||
                        action.id == AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
                })
            } ?: return false
            val action = if (forward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
            return scrollable.performAction(action)
        }

        fun typeText(text: String): Boolean {
            val root = instance?.rootInActiveWindow ?: return false
            val focused = findFirst(root) { it.isFocused && it.isEditable }
                ?: findFirst(root) { it.isEditable }
                ?: return false
            val args = Bundle().apply {
                putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
            }
            return focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }

        fun currentTexts(): List<String> = _snapshot.value

        private fun findFirst(
            node: AccessibilityNodeInfo,
            predicate: (AccessibilityNodeInfo) -> Boolean
        ): AccessibilityNodeInfo? {
            if (predicate(node)) return node
            for (i in 0 until node.childCount) {
                val child = node.getChild(i) ?: continue
                val result = findFirst(child, predicate)
                if (result != null) return result
            }
            return null
        }
    }

    override fun onServiceConnected() {
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val root = rootInActiveWindow ?: return
        _snapshot.value = read(root).take(120)
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        instance = null
        _snapshot.value = emptyList()
        super.onDestroy()
    }

    private fun findText(text: String): AccessibilityNodeInfo? =
        rootInActiveWindow?.let { root ->
            findFirst(root) { it.text?.toString()?.equals(text, ignoreCase = true) == true }
        }

    private fun findDescription(text: String): AccessibilityNodeInfo? =
        rootInActiveWindow?.let { root ->
            findFirst(root) { it.contentDescription?.toString()?.equals(text, ignoreCase = true) == true }
        }

    private fun read(node: AccessibilityNodeInfo): List<String> = buildList {
        node.text?.toString()?.takeIf { it.isNotBlank() }?.let(::add)
        node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let(::add)
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { addAll(read(it)) }
        }
    }
}
