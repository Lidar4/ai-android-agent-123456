package com.example.aiandroidagent.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AIAccessibilityService : AccessibilityService() {
 companion object { private var instance: AIAccessibilityService? = null; private val _snapshot = MutableStateFlow<List<String>>(emptyList()); val snapshot = _snapshot.asStateFlow(); val connected get() = instance != null
  fun clickText(text: String): Boolean = instance?.find(text)?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
  fun clickDescription(description: String): Boolean = instance?.findDescription(description)?.performAction(AccessibilityNodeInfo.ACTION_CLICK) == true
  fun back(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_BACK) == true
  fun home(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_HOME) == true
  fun recents(): Boolean = instance?.performGlobalAction(GLOBAL_ACTION_RECENTS) == true
  fun swipe(x1: Float,y1: Float,x2: Float,y2: Float,duration: Long): Boolean { val s=instance ?: return false; val p=Path().apply{moveTo(x1,y1);lineTo(x2,y2)}; return s.dispatchGesture(GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(p,0,duration)).build(),null,null) }
 }
 override fun onServiceConnected() { instance=this }
 override fun onAccessibilityEvent(event: AccessibilityEvent?) { val root=rootInActiveWindow ?: return; _snapshot.value=read(root).take(80) }
 override fun onInterrupt() {}
 override fun onDestroy() { instance=null; super.onDestroy() }
 private fun find(text:String): AccessibilityNodeInfo? = rootInActiveWindow?.let { search(it){ it.text?.toString()?.equals(text,true)==true } }
 private fun findDescription(text:String): AccessibilityNodeInfo? = rootInActiveWindow?.let { search(it){ it.contentDescription?.toString()?.equals(text,true)==true } }
 private fun search(node:AccessibilityNodeInfo, predicate:(AccessibilityNodeInfo)->Boolean):AccessibilityNodeInfo? { if(predicate(node)) return node; for(i in 0 until node.childCount) node.getChild(i)?.let { search(it,predicate)?.let{return it} }; return null }
 private fun read(node:AccessibilityNodeInfo):List<String> = buildList { node.text?.toString()?.takeIf{it.isNotBlank()}?.let{add(it)}; for(i in 0 until node.childCount) node.getChild(i)?.let{addAll(read(it))} }
}
