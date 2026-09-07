package com.example.aiandroidagent.data

import android.content.Context
import androidx.room.Room
object DatabaseProvider{fun create(context:Context)=Room.databaseBuilder(context,AppDatabase::class.java,"agent.db").fallbackToDestructiveMigration().build()}
