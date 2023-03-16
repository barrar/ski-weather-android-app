package com.example.android.roomyweather.data

import com.example.android.roomyweather.api.WeatherAlertService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherAlertRepository(
    private val service: WeatherAlertService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadAlert(point: String?) : Result<WeatherAlertReport> {
        return withContext(ioDispatcher) {
            try {
                val alert = service.weatherAlertReport(point)
                Result.success(alert)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}