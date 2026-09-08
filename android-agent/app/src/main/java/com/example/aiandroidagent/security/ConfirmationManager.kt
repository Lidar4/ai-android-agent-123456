package com.example.aiandroidagent.security

import com.example.aiandroidagent.agent.AgentAction

class ConfirmationManager {
    fun requiresConfirmation(action: AgentAction): Boolean =
        ActionPolicy().classify(action) == PolicyClass.CONFIRMATION_REQUIRED
}
