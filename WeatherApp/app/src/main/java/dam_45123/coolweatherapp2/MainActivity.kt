package dam_45123.coolweatherapp2

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val lati = 0f
    val longi = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val weatherImage = findViewById<ImageView>(R.id.weatherImage)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val latString = findViewById<EditText>(R.id.latitude).getText().toString()
            val longString = findViewById<EditText>(R.id.longitude).getText().toString()
            val lat = latString.toFloat()
            val long = longString.toFloat()
            fetchWeatherData(lat,long)
        }
    }

        private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData {
        val reqString = buildString {
            append ("https://api.open-meteo.com/v1/forecast?")
            append("latitude=${lat}&longitude=${ long }&")
            append("current_weather=true&")
            append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m")
        }
        val str = reqString.toString()
        val url = URL(reqString.toString())
        url.openStream().use {
            val request = Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
            return request
        }
    }

    private fun fetchWeatherData(lat: Float, long: Float) {
        Thread {
            val weather = WeatherAPI_Call(lat, long)
            updateUI(weather)
        }.start()
    }

    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            val weatherImage: ImageView = findViewById(R.id.weatherImage)
            val seaLevelPressure: TextView = findViewById(R.id.seaLevelPressure)
            val windDirection: TextView = findViewById(R.id.windDirection)
            val windSpeed: TextView = findViewById(R.id.windSpeed)
            val temperature: TextView = findViewById(R.id.temperature)
            val time: TextView = findViewById(R.id.time)
            val day = true

            windDirection.text = request.current_weather.winddirection.toString()
            windSpeed.text = request.current_weather.windspeed.toString() + " Km/h"
            temperature.text = request.current_weather.temperature.toString() + " ºC"
            time.text = request.current_weather.time


            val mapt = getWeatherCodeMap();
            val wCode = mapt.get(request.current_weather.weathercode)
            val wImage = when (wCode) {
                WMO_WeatherCode.CLEAR_SKY,
                WMO_WeatherCode.MAINLY_CLEAR,
                WMO_WeatherCode.PARTLY_COULDY,
                WMO_WeatherCode.FREEZING_DRIZZLE_LIGHT,
                WMO_WeatherCode.FREEZING_DRIZZLE_DENSE,
                WMO_WeatherCode.FREEZING_RAIN_LIGHT,
                WMO_WeatherCode.RAIN_SLIGHT,
                WMO_WeatherCode.RAIN_MODERATE,
                WMO_WeatherCode.RAIN_SHOWERS_SLIGHT,
                WMO_WeatherCode.DRIZZLE_LIGHT,
                WMO_WeatherCode.DRIZZLE_MODERATE,
                WMO_WeatherCode.RAIN_SHOWERS_MODERATE,
                WMO_WeatherCode.SNOW_FALL_SLIGHT->if(day)wCode?.image+"day" else wCode?.image+"night"
                else -> wCode?.image
            }
            val res = resources
            //weatherImage.setImageResource(R.drawable.fog)
            val resID = res.getIdentifier(wImage, "drawable", getPackageName())
            val drawable = this.getDrawable(resID)
            weatherImage.setImageDrawable(drawable)
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.button -> {
                fetchWeatherData(lati, longi)
            }
        }
    }
}