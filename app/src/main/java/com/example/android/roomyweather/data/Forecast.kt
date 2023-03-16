package com.example.android.roomyweather.data

import com.squareup.moshi.FromJson
import java.io.Serializable

/**
 * This class represents an element from the `list` portion of the response from a call to
 * OpenWeather's Five Day Forecast API: https://openweathermap.org/forecast5.
 */
data class Forecast(
    val epoch: Int = 0,
    val temp: Int = 0,
    val tempFeelsLike: Int = 0,
    val cloudCover: Int = 0,
    val windSpeed: Int = 0,
    val windDirDeg: Int = 0,
    val description: String = "",
    val iconUrl: String = "",
    val pop: Int? = 0
) : Serializable

/* ******************************************************************************************
 * Below is a set of classes used to parse the JSON response from the OpenWeather API into
 * a ForecastPeriod object.  The first several classes are designed to match the structure
 * of one element of the `list` field in the OpenWeather 5-day forecast API's JSON response.
 * The last is a custom type adapter that can be used with Moshi to parse OpenWeather JSON
 * directly into a ForecastPeriod object.
 * ******************************************************************************************/

data class OpenWeatherForecastJson(
    val dt: Int,
    val temp: Double,
    val feels_like: Double,
    val clouds: Int,
    val wind_speed: Double,
    val wind_deg: Int,
    val weather: List<OpenWeatherListWeatherJson>,
    val pop: Double?,
)

data class OpenWeatherListWeatherJson(
    val description: String,
    val icon: String
)

class OpenWeatherHourlyJsonAdapter {
    @FromJson
    fun forecastHourlyFromJson(hourly: OpenWeatherForecastJson) = Forecast(
        epoch = hourly.dt,
        temp = hourly.temp.toInt(),
        tempFeelsLike = hourly.feels_like.toInt(),
        cloudCover = hourly.clouds,
        windSpeed = hourly.wind_speed.toInt(),
        windDirDeg = hourly.wind_deg,
        description = hourly.weather[0].description,
        iconUrl = "https://openweathermap.org/img/wn/${hourly.weather[0].icon}@4x.png",
        pop = (hourly.pop!! * 100).toInt()
    )
}

class OpenWeatherCurrentJsonAdapter {
    @FromJson
    fun forecastCurrentFromJson(current: OpenWeatherForecastJson) = Forecast(
        epoch = current.dt,
        temp = current.temp.toInt(),
        tempFeelsLike = current.feels_like.toInt(),
        cloudCover = current.clouds,
        windSpeed = current.wind_speed.toInt(),
        windDirDeg = current.wind_deg,
        description = current.weather[0].description,
        iconUrl = "https://openweathermap.org/img/wn/${current.weather[0].icon}@4x.png",
        pop = null
    )
}