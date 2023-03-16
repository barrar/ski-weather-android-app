package com.example.android.roomyweather.data

import com.squareup.moshi.FromJson
import java.io.Serializable

data class WeatherAlert (
    val area: String,
    val headline: String,
    val description: String
) : Serializable


data class WeatherAlertProperties(
    val areaDesc: String,
    val headline: String,
    val description: String
)

data class WeatherAlertJson(
    val properties: WeatherAlertProperties
)

class WeatherAlertJsonAdapter {
    @FromJson
    fun weatherAlertFromJson(features: WeatherAlertJson) = WeatherAlert(
        area = features.properties.areaDesc,
        headline = features.properties.headline,
        description = features.properties.description
    )
}