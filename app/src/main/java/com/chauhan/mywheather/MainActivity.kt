package com.chauhan.mywheather

import MyWheather
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.chauhan.mywheather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//085bacaf22203e5fea3e1eef53e0edaa

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("jaipur")
        searchCity()
    }

    private fun searchCity(){
        val searchCity=binding.searchView
        searchCity.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(city: String) {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(city, "085bacaf22203e5fea3e1eef53e0edaa", "metric")
        response.enqueue(object : Callback<MyWheather> {
            override fun onResponse(call: Call<MyWheather>, response: Response<MyWheather>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val sunrise = responseBody.sys.sunrise.toLong()
                    val sunset = responseBody.sys.sunrise.toLong()
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val windSpeed = responseBody.wind.speed
                    val seaLevel = responseBody.main.pressure

                    binding.temp.text = "$temperature"
                    binding.max.text = "Max temp $maxTemp ℃"
                    binding.min.text = "Min temp $minTemp ℃"
                    binding.weather.text = condition
                    binding.condition.text = condition
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sealevel.text = "$seaLevel hPa"
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    binding.humidity.text = "$humidity %"
                    binding.city.text = "$city"
                    binding.day.text = dayName(System.currentTimeMillis())
                    binding.date.text=date()

                    changeImage(condition)
                }
            }

            override fun onFailure(call: Call<MyWheather>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImage(conditions:String) {
        when(conditions){
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunshine)
                binding.lottieAnimationView.setAnimation(R.raw.sunj)
            }
            "Partly Clouds","Clouds"->{
                binding.root.setBackgroundResource(R.drawable.overcast)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.foggy)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Overcast"->{
                binding.root.setBackgroundResource(R.drawable.clouds)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "Light Rain","Drizzle","Moderate Rain"->{
                binding.root.setBackgroundResource(R.drawable.rainy)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.heavyrain)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snowt)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }else->{
                binding.root.setBackgroundResource(R.drawable.sunshine)
                binding.lottieAnimationView.setAnimation(R.raw.sunj)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }

     fun date():String {
         val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
         return sdf.format(Date())
    }
     fun time(timestamp: Long):String {
         val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
         return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

}