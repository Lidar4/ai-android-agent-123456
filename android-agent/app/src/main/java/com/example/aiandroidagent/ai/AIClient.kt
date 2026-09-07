package com.example.aiandroidagent.ai

import com.example.aiandroidagent.agent.*
import kotlinx.serialization.json.Json

interface AIClient { suspend fun createPlan(command: String, deviceState: DeviceState): Result<AgentPlan>; suspend fun analyzeScreen(image: ByteArray): Result<String> }

class GeminiProvider(private val endpoint: String? = null): AIClient {
 private val json = Json { ignoreUnknownKeys = false; classDiscriminator = "type" }
 override suspend fun createPlan(command: String, deviceState: DeviceState): Result<AgentPlan> = runCatching {
  val lower = command.lowercase()
  if (lower.contains("youtube")) AgentPlan("Open YouTube", listOf(AgentAction.OpenApp("com.google.android.youtube"), AgentAction.Wait(1000), AgentAction.Observe())) else AgentPlan(command, listOf(AgentAction.Observe(), AgentAction.AskUserConfirmation("No remote planner configured. Review this task before continuing.")))
 }
 override suspend fun analyzeScreen(image: ByteArray): Result<String> = Result.success("Screen analysis is available when a secure backend endpoint is configured.")
 fun parsePlan(raw: String): Result<AgentPlan> = runCatching { json.decodeFromString<AgentPlan>(raw) }
}
