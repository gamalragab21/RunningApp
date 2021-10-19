package com.example.runningapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Database(
    entities = [Run::class],version = 1
)
@TypeConverters(Converts::class)
abstract class RunningDataBase :RoomDatabase() {
    abstract fun getRunDao():RunDao

    // all thing dagger will handle it so u don't need singleton of object database
}