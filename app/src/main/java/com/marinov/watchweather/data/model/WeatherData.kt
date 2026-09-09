package com.marinov.watchweather.data.model

data class WeatherData(
    val city: String,
    val temperature: String,
    val sensation: String,
    val wind: String,
    val humidity: String,
    val pressure: String,
    val airQuality: String
)