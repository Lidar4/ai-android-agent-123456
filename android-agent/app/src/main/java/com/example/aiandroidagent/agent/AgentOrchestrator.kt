package com.example.aiandroidagent.agent

import com.example.aiandroidagent.ai.AIClient
import com.example.aiandroidagent.security.ActionPolicy
import kotlinx.coroutines.*

class AgentOrchestrator(private val ai: AIClient, private val policy: ActionPolicy, private val executor: suspend (AgentAction) -> ActionResult) {
 private var job: Job? = null
 fun cancel() { job?.cancel() }
 fun run(command:String, state: suspend () -> DeviceState, onEvent:(String)->Unit, simulate:Boolean=false) { cancel(); job=CoroutineScope(SupervisorJob()+Dispatchers.Default).launch { try { onEvent("Observing device"); var plan=ai.createPlan(command,state()).getOrThrow(); policy.validate(plan).getOrThrow(); onEvent("Plan: ${plan.goal}"); var attempts=0; for(action in plan.actions) { ensureActive(); if(attempts++>=30) error("Action limit reached"); onEvent(if(simulate) "Simulated ${action::class.simpleName}" else "Executing ${action::class.simpleName}"); if(!simulate){ val result=withTimeout(10_000){executor(action)}; onEvent(result.message); if(!result.success) { onEvent("Replanning after failure"); plan=ai.createPlan(command,state()).getOrThrow() } } if(action is AgentAction.Stop) break }; onEvent("Task complete") } catch(e:CancellationException){ onEvent("Stopped by user") } catch(e:Exception){ onEvent("Task failed: ${e.message}") } } }
}
