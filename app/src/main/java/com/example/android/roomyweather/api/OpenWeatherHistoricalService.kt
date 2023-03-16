package com.example.android.roomyweather.api

import com.example.android.roomyweather.data.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime

interface OpenWeatherHistoricalService {
    @GET("onecall/timemachine")
    suspend fun historicalSnowfall(
        @Query("lat") latitude: String?,
        @Query("lon") longitude: String?,
        @Query("dt") epoch: Int? = (System.currentTimeMillis()/1000 - 86400).toInt(), // minus 1 day
        @Query("units") units: String? = "imperial",
        @Query("appid") apiKey: String = "497c6bf127255fe6edab120bb30041e2"
    ) : HistoricalSnowfall

    companion object {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        /**
         * This method is used to create an instance of the [OpenWeatherService] interface.
         */
        fun create() : OpenWeatherHistoricalService {
            val moshi = Moshi.Builder()
                .add(HistoricalSnowfallJsonAdapter())
                .addLast(KotlinJsonAdapterFactory())
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenWeatherHistoricalService::class.java)
        }
    }
}