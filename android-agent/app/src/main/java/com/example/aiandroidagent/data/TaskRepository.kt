package com.example.aiandroidagent.data

class TaskRepository(private val dao:TaskDao){suspend fun save(command:String,status:String)=dao.insert(TaskEntity(command=command,status=status));suspend fun history()=dao.all();suspend fun clear()=dao.clear()}
