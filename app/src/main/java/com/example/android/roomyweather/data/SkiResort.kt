package com.example.android.roomyweather.data

import com.example.android.roomyweather.R

data class SkiResort(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val website: String,
    val trailMapResID: Int,
    var forecast: List<Forecast> = listOf(),
    var snowfall: String = "",
    var alerts: List<WeatherAlert> = listOf()
)

fun initializeSkiResorts(): List<SkiResort>{
    val skiResortList = mutableListOf<SkiResort>()
    skiResortList.add(
        SkiResort(
        "Mt. Bachelor",
        44.00,
        -121.68,
        "https://www.mtbachelor.com/",
        R.drawable.mt_bachelor_map
        ))
    skiResortList.add(
        SkiResort(
        "Timberline Lodge",
        45.33,
        -121.71,
        "https://www.timberlinelodge.com/",
        R.drawable.timberline_map
        ))
    skiResortList.add(
        SkiResort(
        "Mt. Hood Meadows",
        45.33,
        -121.67,
        "https://www.skihood.com/",
        R.drawable.meadows_map
        ))
    skiResortList.add(
        SkiResort(
        "Hoodoo",
        44.41,
        -121.87,
        "https://skihoodoo.com/",
        R.drawable.hoodoo_map
        ))
    skiResortList.add(
        SkiResort(
        "Willamette Pass",
        43.60,
        -122.04,
        "https://willamettepass.com/",
        R.drawable.willamette_map
        ))
    skiResortList.add(
        SkiResort(
            "Alert Demo",
            31.36,
            -100.49,
            "https://www.mtbachelor.com/",
            R.drawable.willamette_map
        )
    )
    return skiResortList
}