package com.example.weatherapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class WeatherDB(
    @PrimaryKey(autoGenerate = true) val registerID:Int,
    @ColumnInfo(name="city_name") val cityName:String?,
    @ColumnInfo(name = "api_log") val apiLog:String?,

)
{constructor(cityName: String?,apiLog: String?):
this(0,cityName,apiLog)}
