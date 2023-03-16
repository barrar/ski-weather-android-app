package com.example.android.roomyweather.data

import com.squareup.moshi.Json

data class CurrentWeatherReport (
    @Json(name = "current") val current: Forecast,
    @Json(name = "hourly") val periods: List<Forecast>
)
