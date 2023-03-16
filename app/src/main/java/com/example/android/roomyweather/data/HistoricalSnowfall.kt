package com.example.android.roomyweather.data

import com.squareup.moshi.Json

data class HistoricalSnowfall(
    @Json(name="hourly")
    val hourlySnowfall: List<Snowfall>
)