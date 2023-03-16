package com.example.android.roomyweather.data

import com.example.android.roomyweather.api.OpenWeatherCurrentService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrentForecastRepository(
    private val service: OpenWeatherCurrentService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun loadForecast(lat: String?, long: String?): Result<CurrentWeatherReport> {
        return run {
            withContext(ioDispatcher) {
                try {
                    val forecast = service.currentWeatherReport(lat, long)
                    Result.success(forecast)
                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }
}