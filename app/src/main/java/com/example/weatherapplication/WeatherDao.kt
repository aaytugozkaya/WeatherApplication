package com.example.weatherapplication

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weatherdb")
    fun getAll(): List<WeatherDB>
    @Query("SELECT * FROM weatherdb WHERE registerID IN (:registerIds)")
    fun loadAllByIds(registerIds: IntArray): List<WeatherDB>
    @Insert
    fun insertAll(vararg users: WeatherDB)
    @Delete
    fun delete(user: WeatherDB) }