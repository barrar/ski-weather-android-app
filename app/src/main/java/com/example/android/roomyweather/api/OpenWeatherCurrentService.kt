package com.example.android.roomyweather.api

import com.example.android.roomyweather.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherCurrentService {

    @GET("onecall")
    suspend fun currentWeatherReport(
        @Query("lat") latitude: String?,
        @Query("lon") longitude: String?,
        @Query("exclude") excludedData: String? = "minutely,daily",
        @Query("units") units: String? = "imperial",
        @Query("appid") apiKey: String = "497c6bf127255fe6edab120bb30041e2"
    ) : CurrentWeatherReport

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        /**
         * This method is used to create an instance of the [OpenWeatherService] interface.
         */
        fun create() : OpenWeatherCurrentService {
            val moshi = Moshi.Builder()
                .add(OpenWeatherCurrentJsonAdapter())
                .add(OpenWeatherHourlyJsonAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenWeatherCurrentService::class.java)
        }
    }
}