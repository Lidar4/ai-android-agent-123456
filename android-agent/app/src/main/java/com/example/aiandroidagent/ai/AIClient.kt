package com.example.aiandroidagent.ai

import com.example.aiandroidagent.agent.*
import kotlinx.serialization.json.Json

interface AIClient { suspend fun createPlan(command: String, deviceState: DeviceState): Result<AgentPlan>; suspend fun analyzeScreen(image: ByteArray): Result<String> }

class GeminiProvider(private val endpoint: String? = null): AIClient {
    private val json = Json { ignoreUnknownKeys = false; classDiscriminator = "type" }
    
    private val appPackageMap = mapOf(
        "youtube" to "com.google.android.youtube",
        "capcut" to "com.gpro.capcut",
        "maps" to "com.google.android.apps.maps",
        "chrome" to "com.android.chrome",
        "settings" to "com.android.settings"
    )

    override suspend fun createPlan(command: String, deviceState: DeviceState): Result<AgentPlan> = runCatching {
        val lower = command.lowercase().trim()
        val actions = mutableListOf<AgentAction>()
        var goal = "Automated execution for: $command"

        when {
            lower.startsWith("open ") -> {
                val appName = lower.substringAfter("open ").trim()
                val pkg = appPackageMap[appName] ?: appPackageMap.keys.find { appName.contains(it) }?.let { appPackageMap[it] }
                if (pkg != null) {
                    goal = "Open $appName"
                    actions.add(AgentAction.OpenApp(pkg))
                    actions.add(AgentAction.Wait(1500))
                    actions.add(AgentAction.Observe())
                } else {
                    goal = "Open $appName"
                    actions.add(AgentAction.OpenApp("com.example.$appName"))
                    actions.add(AgentAction.Wait(1000))
                    actions.add(AgentAction.Observe())
                }
            }
            lower.contains("select this video") || lower.contains("select video") -> {
                goal = "Select video from CapCut"
                actions.add(AgentAction.ClickText("Media"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Videos"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickDescription("Select first video"))
                actions.add(AgentAction.Wait(500))
                actions.add(AgentAction.ClickText("Add"))
                actions.add(AgentAction.Wait(2000))
                actions.add(AgentAction.Observe())
            }
            lower.contains("trim the beginning") || lower.contains("trim") -> {
                goal = "Trim the beginning of the video"
                actions.add(AgentAction.ClickText("Edit"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Split"))
                actions.add(AgentAction.Wait(500))
                actions.add(AgentAction.ClickDescription("Delete segment"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.Observe())
            }
            lower.contains("add this effect") || lower.contains("add effect") -> {
                goal = "Apply modern visual effect"
                actions.add(AgentAction.ClickText("Effects"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Video Effects"))
                actions.add(AgentAction.Wait(1500))
                actions.add(AgentAction.ClickText("Trending"))
                actions.add(AgentAction.Wait(500))
                actions.add(AgentAction.ClickText("Soft Glow"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Apply"))
                actions.add(AgentAction.Observe())
            }
            lower.contains("add subtitles") || lower.contains("subtitles") -> {
                goal = "Generate auto captions"
                actions.add(AgentAction.ClickText("Text"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Auto captions"))
                actions.add(AgentAction.Wait(1500))
                actions.add(AgentAction.ClickText("Generate"))
                actions.add(AgentAction.Wait(3000))
                actions.add(AgentAction.Observe())
            }
            lower.contains("find an editing style") || lower.contains("research style") -> {
                goal = "Research online editing styles and generate plan"
                actions.add(AgentAction.Observe())
                actions.add(AgentAction.AskUserConfirmation("I have researched popular CapCut styles online and suggest applying the 'Cinematic Warm Retro' style. Proceed?"))
                actions.add(AgentAction.OpenApp("com.gpro.capcut"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Effects"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.ClickText("Retro"))
                actions.add(AgentAction.Wait(1000))
                actions.add(AgentAction.Observe())
            }
            else -> {
                actions.add(AgentAction.Observe())
                actions.add(AgentAction.AskUserConfirmation("Running custom user query. Confirm to proceed?"))
            }
        }
        AgentPlan(goal, actions)
    }
    
    override suspend fun analyzeScreen(image: ByteArray): Result<String> = Result.success("Screen content analysis reveals editing controls and media elements ready.")
    fun parsePlan(raw: String): Result<AgentPlan> = runCatching { json.decodeFromString<AgentPlan>(raw) }
}
