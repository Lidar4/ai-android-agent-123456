package com.example.aiandroidagent.agent

import com.example.aiandroidagent.ai.AIClient
import com.example.aiandroidagent.security.ActionPolicy
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class AgentOrchestrator(
    private val ai: AIClient,
    private val policy: ActionPolicy,
    private val executor: suspend (AgentAction) -> ActionResult
) {
    private var job: Job? = null

    fun cancel() {
        job?.cancel()
        job = null
    }

    fun run(
        command: String,
        state: suspend () -> DeviceState,
        onEvent: (String) -> Unit,
        simulate: Boolean = false
    ) {
        cancel()

        job = CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                onEvent("Observing device")

                var plan = ai.createPlan(command, state()).getOrThrow()
                policy.validate(plan).getOrThrow()
                onEvent("Plan: ${plan.goal}")

                var actionIndex = 0
                var attempts = 0

                while (actionIndex < plan.actions.size) {
                    ensureActive()

                    if (attempts++ >= 30) {
                        error("Action limit reached")
                    }

                    val action = plan.actions[actionIndex]
                    onEvent(
                        if (simulate) {
                            "Simulated ${action::class.simpleName}"
                        } else {
                            "Executing ${action::class.simpleName}"
                        }
                    )

                    if (!simulate) {
                        val result = withTimeout(10_000) {
                            executor(action)
                        }
                        onEvent(result.message)

                        if (!result.success) {
                            onEvent("Replanning after failure")
                            plan = ai.createPlan(command, state()).getOrThrow()
                            policy.validate(plan).getOrThrow()
                            onEvent("Replanned: ${plan.goal}")
                            actionIndex = 0
                            continue
                        }
                    }

                    actionIndex++

                    if (action is AgentAction.Stop) {
                        break
                    }
                }

                onEvent("Task complete")
            } catch (e: CancellationException) {
                onEvent("Stopped by user")
            } catch (e: Exception) {
                onEvent("Task failed: ${e.message ?: "Unknown error"}")
            }
        }
    }
}
