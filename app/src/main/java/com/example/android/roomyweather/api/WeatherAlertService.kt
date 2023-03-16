package com.example.android.roomyweather.api

import com.example.android.roomyweather.data.OpenWeatherCurrentJsonAdapter
import com.example.android.roomyweather.data.OpenWeatherHourlyJsonAdapter
import com.example.android.roomyweather.data.WeatherAlertJsonAdapter
import com.example.android.roomyweather.data.WeatherAlertReport
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAlertService {
    @GET("active")
    suspend fun weatherAlertReport (
        @Query("point") point: String?
    ) : WeatherAlertReport

    companion object {
        private const val BASE_URL = "https://api.weather.gov/alerts/"
        fun create() : WeatherAlertService {
            val moshi = Moshi.Builder()
                .add(WeatherAlertJsonAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WeatherAlertService::class.java)
        }
    }
}