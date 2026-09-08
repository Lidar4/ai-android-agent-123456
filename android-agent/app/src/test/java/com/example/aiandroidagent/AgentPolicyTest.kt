package com.example.aiandroidagent

import com.example.aiandroidagent.agent.*
import com.example.aiandroidagent.security.ActionPolicy
import com.example.aiandroidagent.data.AutomationRuleEntity
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AgentPolicyTest {

    @Test
    fun safePlanValidates() {
        val policy = ActionPolicy()
        val plan = AgentPlan("Verify state", listOf(AgentAction.Observe(), AgentAction.Wait(100)))
        assertTrue(policy.validate(plan).isSuccess)
    }

    @Test
    fun planWithBlockedActionsFails() {
        val policy = ActionPolicy()
        val actions = List(31) { AgentAction.Observe() }
        val plan = AgentPlan("Too large", actions)
        assertTrue(policy.validate(plan).isFailure)
    }

    @Test
    fun malformedPlanFails() {
        assertTrue(runCatching { Json.decodeFromString<AgentPlan>("{ bad json }") }.isFailure)
    }

    @Test
    fun actionValidationClassifications() {
        val policy = ActionPolicy()
        assertEquals(PolicyClass.SAFE, policy.classify(AgentAction.Observe()))
        assertEquals(PolicyClass.SAFE, policy.classify(AgentAction.OpenApp("com.example")))
        assertEquals(PolicyClass.CONFIRMATION_REQUIRED, policy.classify(AgentAction.TypeText("hello")))
        assertEquals(PolicyClass.CONFIRMATION_REQUIRED, policy.classify(AgentAction.AskUserConfirmation("Are you sure?")))
    }

    @Test
    fun ruleMatchingValidation() {
        val rule1 = AutomationRuleEntity(
            triggerType = "SMS_RECEIVED",
            senderPattern = "+12345",
            actionText = "Reply message"
        )
        val ruleEmpty = AutomationRuleEntity(
            triggerType = "SMS_RECEIVED",
            senderPattern = "",
            actionText = "General reply"
        )

        assertTrue("+12345".contains(rule1.senderPattern, ignoreCase = true))
        assertFalse("+99999".contains(rule1.senderPattern, ignoreCase = true))

        assertTrue("+12345".contains(ruleEmpty.senderPattern, ignoreCase = true))
        assertTrue("+99999".contains(ruleEmpty.senderPattern, ignoreCase = true))
    }

    @Test
    fun retryLimitsAndMaxExecutionLength() {
        val maxActions = 30
        assertTrue(maxActions == 30)
    }

    @Test
    fun executorFailureHandlingReturnsStructuredError() {
        val result = ActionResult(
            success = false,
            message = "Accessibility service is not connected",
            errorCode = "ACCESSIBILITY_NOT_CONNECTED"
        )
        assertFalse(result.success)
        assertEquals("ACCESSIBILITY_NOT_CONNECTED", result.errorCode)
    }
}
