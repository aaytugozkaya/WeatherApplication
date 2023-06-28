package com.example.weatherapplication

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapplication.WeatherDB
import com.example.weatherapplication.WeatherDao

@Database(entities = arrayOf(WeatherDB::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherdao(): WeatherDao
}