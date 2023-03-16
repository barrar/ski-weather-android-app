package com.example.android.roomyweather.data

import com.example.android.roomyweather.api.OpenWeatherCurrentService
import com.example.android.roomyweather.api.OpenWeatherHistoricalService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class HistoricalForecastRepository (
    private val service: OpenWeatherHistoricalService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun loadForecast(
        lat: String?,
        long: String?
    ) : Result<HistoricalSnowfall> {
        return withContext(ioDispatcher) {
            try {
                val snowfall = service.historicalSnowfall(lat, long)
                Result.success(snowfall)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}