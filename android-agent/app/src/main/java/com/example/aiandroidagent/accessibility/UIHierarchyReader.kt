package com.example.aiandroidagent.accessibility

import android.view.accessibility.AccessibilityNodeInfo
class UIHierarchyReader { fun read(root:AccessibilityNodeInfo?):List<String> { if(root==null)return emptyList(); val out=mutableListOf<String>(); fun visit(n:AccessibilityNodeInfo){n.text?.toString()?.takeIf{it.isNotBlank()}?.let(out::add);n.contentDescription?.toString()?.takeIf{it.isNotBlank()}?.let(out::add);for(i in 0 until n.childCount)n.getChild(i)?.let(::visit)};visit(root);return out.distinct()} }
