package com.application.autoapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataAuto::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dataAutoDao(): DataAutoDao
}