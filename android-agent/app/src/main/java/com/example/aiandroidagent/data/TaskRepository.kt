package com.example.aiandroidagent.data

class TaskRepository(private val db: AppDatabase) {
    private val taskDao = db.tasks()
    private val conversationDao = db.conversations()
    private val ruleDao = db.rules()
    private val settingDao = db.settings()

    // Tasks
    suspend fun save(command: String, status: String) = taskDao.insert(TaskEntity(command = command, status = status))
    suspend fun history() = taskDao.all()
    suspend fun clear() = taskDao.clear()

    // Conversations
    suspend fun getConversations(): List<ConversationEntity> = conversationDao.all()
    suspend fun addConversation(role: String, content: String) = conversationDao.insert(ConversationEntity(role = role, content = content))
    suspend fun clearConversations() = conversationDao.clear()

    // Rules
    suspend fun getRules(): List<AutomationRuleEntity> = ruleDao.all()
    suspend fun addRule(triggerType: String, senderPattern: String, actionText: String) = ruleDao.insert(AutomationRuleEntity(triggerType = triggerType, senderPattern = senderPattern, actionText = actionText))
    suspend fun updateRule(rule: AutomationRuleEntity) = ruleDao.update(rule)
    suspend fun deleteRule(rule: AutomationRuleEntity) = ruleDao.delete(rule)

    // Settings
    suspend fun getSetting(key: String): String? = settingDao.get(key)?.value
    suspend fun saveSetting(key: String, value: String) = settingDao.put(SettingEntity(key, value))
}

