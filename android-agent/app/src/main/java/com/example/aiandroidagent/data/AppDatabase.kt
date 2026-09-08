package com.example.aiandroidagent.data

import androidx.room.*

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val command: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "agent"
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class AutomationRuleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val triggerType: String, // "SMS_RECEIVED" or "NOTIFICATION_RECEIVED"
    val senderPattern: String,
    val actionText: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM TaskEntity ORDER BY createdAt DESC")
    suspend fun all(): List<TaskEntity>

    @Insert
    suspend fun insert(task: TaskEntity)

    @Query("DELETE FROM TaskEntity")
    suspend fun clear()
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM ConversationEntity ORDER BY createdAt ASC")
    suspend fun all(): List<ConversationEntity>

    @Insert
    suspend fun insert(message: ConversationEntity)

    @Query("DELETE FROM ConversationEntity")
    suspend fun clear()
}

@Dao
interface AutomationRuleDao {
    @Query("SELECT * FROM AutomationRuleEntity ORDER BY createdAt DESC")
    suspend fun all(): List<AutomationRuleEntity>

    @Insert
    suspend fun insert(rule: AutomationRuleEntity)

    @Update
    suspend fun update(rule: AutomationRuleEntity)

    @Delete
    suspend fun delete(rule: AutomationRuleEntity)
}

@Dao
interface SettingDao {
    @Query("SELECT * FROM SettingEntity WHERE `key` = :key LIMIT 1")
    suspend fun get(key: String): SettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(setting: SettingEntity)
}

@Database(
    entities = [TaskEntity::class, ConversationEntity::class, AutomationRuleEntity::class, SettingEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tasks(): TaskDao
    abstract fun conversations(): ConversationDao
    abstract fun rules(): AutomationRuleDao
    abstract fun settings(): SettingDao
}

