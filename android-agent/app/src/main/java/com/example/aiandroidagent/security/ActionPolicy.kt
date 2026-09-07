package com.example.aiandroidagent.security

import com.example.aiandroidagent.agent.*

class ActionPolicy {
 fun classify(action: AgentAction): PolicyClass = when (action) {
  is AgentAction.OpenApp, is AgentAction.ClickText, is AgentAction.ClickDescription, is AgentAction.ClickCoordinate, is AgentAction.Swipe, is AgentAction.Scroll, is AgentAction.Back, is AgentAction.Home, is AgentAction.Recents, is AgentAction.Wait, is AgentAction.Observe, is AgentAction.ScreenAnalyze -> PolicyClass.SAFE
  is AgentAction.TypeText, is AgentAction.AskUserConfirmation -> PolicyClass.CONFIRMATION_REQUIRED
  is AgentAction.Stop -> PolicyClass.SAFE
 }
 fun validate(plan: AgentPlan): Result<Unit> = if (plan.actions.size > 30 || plan.actions.any { classify(it) == PolicyClass.BLOCKED }) Result.failure(IllegalArgumentException("Plan contains blocked or excessive actions")) else Result.success(Unit)
}
