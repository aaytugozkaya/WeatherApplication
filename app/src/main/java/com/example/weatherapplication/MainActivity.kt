package com.example.weatherapplication

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.weatherapplication.ui.theme.WeatherApplicationTheme
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private lateinit var location : EditText
val API: String = "********************"
private lateinit var DEFAULT_CITY: String
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        DEFAULT_CITY = "Krakow"
        weatherTask().execute()
        }
    fun runExecution(view:View){
        location =findViewById(R.id.cityText)
        if(findViewById<View?>(R.id.cityText).toString().length>1){
            DEFAULT_CITY= location.text.toString()
        }
        else{
            DEFAULT_CITY = "Krakow"
        }

        weatherTask().execute()

    }
    fun runRefresh(view:View){
        weatherTask().execute()
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$DEFAULT_CITY&units=metric&appid=$API").readText(
                        Charsets.UTF_8
                    )
            } catch (e: Exception) {
                response = "null"
            }
            Log.i("myTag", response.toString())

            val weather = WeatherDB(DEFAULT_CITY,response)
            val db = Room.databaseBuilder( applicationContext, AppDatabase::class.java, "weatherdatabase" ).allowMainThreadQueries().enableMultiInstanceInvalidation() .fallbackToDestructiveMigration().build()
            val weatherDao = db.weatherdao()
            weatherDao.insertAll(weather)
            return response

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                Log.i("myasasa", "inside onPostExec")
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)


                val temp = main.getString("temp") + "°C"
                val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise: Long = sys.getLong("sunrise")
                val sunset: Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherMain = weather.getString("main")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name") + ", " + sys.getString("country")

                /* Populating extracted data into our views */
                findViewById<TextView>(R.id.locationTextView).text = address
                if(weatherMain.equals("Clouds")){
                    if(weatherDescription.equals("few clouds")||weatherDescription.equals("scattered clouds")){
                        findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.fewclouds)
                    }
                    else{
                        findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.cloudy)
                    }
                }
                else if (weatherMain.equals("Clear")){
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.sunny)
                }
                else if (weatherMain.equals("Snow")){
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.snowy)
                }
                else if (weatherMain.equals("Atmosphere")){
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.mist)
                }
                else if (weatherMain.equals("Rain")){
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.rainy)
                }
                else if (weatherMain.equals("Drizzle")){
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.shower)
                }
                else if (weatherMain.equals("Rain")){
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.rainy)
                }
                else{
                    findViewById<ImageView>(R.id.weatherIcon).setBackgroundResource(R.drawable.minus)
                }
                findViewById<TextView>(R.id.condition).text = weatherDescription.capitalize()
                findViewById<TextView>(R.id.temp).text = temp
                findViewById<TextView>(R.id.temp_min).text = tempMin
                findViewById<TextView>(R.id.temp_max).text = tempMax
                findViewById<TextView>(R.id.sunrise).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                findViewById<TextView>(R.id.sunset).text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.Pressure).text = pressure
                findViewById<TextView>(R.id.humidity).text = humidity


            } catch (e: Exception) {
                findViewById<TextView>(R.id.locationTextView).text = "City not found"
            }

        }
    }
}
