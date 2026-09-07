package com.example.aiandroidagent.data

import androidx.room.*

@Entity data class TaskEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val command:String,val status:String,val createdAt:Long=System.currentTimeMillis())
@Entity data class ConversationEntity(@PrimaryKey(autoGenerate=true) val id:Long=0,val role:String,val content:String,val createdAt:Long=System.currentTimeMillis())
@Dao interface TaskDao { @Query("SELECT * FROM TaskEntity ORDER BY createdAt DESC") suspend fun all():List<TaskEntity>; @Insert suspend fun insert(task:TaskEntity); @Query("DELETE FROM TaskEntity") suspend fun clear() }
@Database(entities=[TaskEntity::class,ConversationEntity::class],version=1,exportSchema=false) abstract class AppDatabase:RoomDatabase(){ abstract fun tasks():TaskDao }
