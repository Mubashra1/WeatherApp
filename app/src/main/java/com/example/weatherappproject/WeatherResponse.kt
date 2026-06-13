package com.example.weatherappproject

class WeatherResponse (
        val name: String,
        val main: Main,
        val weather: List<Weather>,
        val wind: Wind
    )

data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int,
    val pressure: Int
)
    data class Weather(
        val description: String
    )

    data class Wind(
        val speed: Double
    )