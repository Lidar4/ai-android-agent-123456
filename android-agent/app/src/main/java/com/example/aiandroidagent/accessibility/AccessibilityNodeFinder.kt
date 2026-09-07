package com.example.aiandroidagent.accessibility

import android.view.accessibility.AccessibilityNodeInfo
class AccessibilityNodeFinder { fun byText(root:AccessibilityNodeInfo?,text:String):AccessibilityNodeInfo?=find(root){it.text?.toString()?.equals(text,true)==true}; fun byDescription(root:AccessibilityNodeInfo?,text:String)=find(root){it.contentDescription?.toString()?.equals(text,true)==true}; private fun find(n:AccessibilityNodeInfo?,p:(AccessibilityNodeInfo)->Boolean):AccessibilityNodeInfo?{if(n==null)return null;if(p(n))return n;for(i in 0 until n.childCount)find(n.getChild(i),p)?.let{return it};return null} }
