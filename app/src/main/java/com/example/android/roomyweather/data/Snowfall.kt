package com.example.android.roomyweather.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import java.io.Serializable

data class Snowfall(
    val amount: Double
) : Serializable

data class HistoricalWeatherJson(
    val snow: HistoricalSnowfallJson?
)

data class HistoricalSnowfallJson(
    @Json(name="1h") val snowInHour: Double
)

class HistoricalSnowfallJsonAdapter {
    @FromJson
    fun historicalSnowfallFromJson(hourly: HistoricalWeatherJson) = Snowfall(
        amount = if (hourly.snow != null) hourly.snow.snowInHour else 0.0
    )
}

