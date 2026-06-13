package com.example.weatherappproject

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weatherappproject.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val API_KEY = "d598e223743bd284c8f5d718f1dfdbf2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener { searchWeather() }

        binding.etCity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWeather()
                true
            } else false
        }
    }

    private fun searchWeather() {
        val city = binding.etCity.text.toString().trim()
        if (city.isEmpty()) {
            binding.tvError.text = "Please enter a city name."
            binding.tvError.visibility = View.VISIBLE
            return
        }

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etCity.windowToken, 0)

        binding.progressBar.visibility = View.VISIBLE
        binding.weatherCard.visibility = View.GONE
        binding.tvError.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getWeather(city, API_KEY)
                if (response.isSuccessful && response.body() != null) {
                    showWeather(response.body()!!)
                } else {
                    showError("City not found. Please try again.")
                }
            } catch (e: Exception) {
                showError("No internet. Please try again.")
            }
        }
    }


    private fun showWeather(data: WeatherResponse) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.weatherCard.visibility = View.VISIBLE

        binding.tvCityName.text = "📍 ${data.name}"
        binding.tvTemperature.text = "${data.main.temp.roundToInt()}°"
        binding.tvDescription.text = data.weather[0].description
            .replaceFirstChar { it.uppercase() }
        binding.tvHumidity.text = "${data.main.humidity}%"
        binding.tvWind.text = "${(data.wind.speed * 3.6).roundToInt()} km/h"

        val desc = data.weather[0].description.lowercase()

        binding.tvWeatherEmoji.text = when {
            desc.contains("thunder") -> "⛈️"
            desc.contains("drizzle") -> "🌦️"
            desc.contains("rain") -> "🌧️"
            desc.contains("snow") -> "❄️"
            desc.contains("mist") || desc.contains("fog") -> "🌫️"
            desc.contains("clear") -> "☀️"
            desc.contains("cloud") -> "☁️"
            else -> "🌤️"
        }

        binding.tvWeatherComment.text = when {
            desc.contains("thunder") -> "⚡ Stay indoors, it's wild out there!"
            desc.contains("drizzle") -> "🌂 Light drizzle — just a little wet!"
            desc.contains("rain") -> "☔ Don't forget your umbrella today!"
            desc.contains("snow") -> "🧣 Bundle up, it's snowy outside!"
            desc.contains("mist") || desc.contains("fog") -> "👀 Foggy day — drive carefully!"
            desc.contains("clear") -> "😎 Beautiful clear sky — enjoy it!"
            desc.contains("cloud") -> "☁️ Cloudy skies — umbrella just in case!"
            else -> "🌤️ Decent weather — go enjoy outside!"
        }
    }
    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.weatherCard.visibility = View.GONE
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
    }
}