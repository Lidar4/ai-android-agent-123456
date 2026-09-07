package com.example.aiandroidagent

import com.example.aiandroidagent.agent.*
import com.example.aiandroidagent.security.ActionPolicy
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AgentPolicyTest { @Test fun safePlanValidates(){assertTrue(ActionPolicy().validate(AgentPlan("x",listOf(AgentAction.Observe()))).isSuccess)} @Test fun malformedPlanFails(){assertTrue(runCatching{Json.decodeFromString<AgentPlan>("{bad")}.isFailure)} @Test fun unknownActionFails(){assertTrue(runCatching{Json.decodeFromString<AgentPlan>("{\"goal\":\"x\",\"actions\":[{\"type\":\"SHELL\"}]}")}.isFailure)} }
