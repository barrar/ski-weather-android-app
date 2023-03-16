package com.example.android.roomyweather.data

import com.squareup.moshi.Json

data class WeatherAlertReport (
    @Json(name = "features") val alerts: List<WeatherAlert>
)