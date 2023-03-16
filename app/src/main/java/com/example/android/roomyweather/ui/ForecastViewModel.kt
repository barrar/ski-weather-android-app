package com.example.android.roomyweather.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.roomyweather.api.OpenWeatherCurrentService
import com.example.android.roomyweather.api.OpenWeatherHistoricalService
import com.example.android.roomyweather.api.WeatherAlertService
import com.example.android.roomyweather.data.*
import kotlinx.coroutines.launch

class ForecastViewModel : ViewModel() {
    private val currentForecastRepository = CurrentForecastRepository(OpenWeatherCurrentService.create())
    private val historicalForecastRepository = HistoricalForecastRepository(OpenWeatherHistoricalService.create())
    private val weatherAlertRepository = WeatherAlertRepository(WeatherAlertService.create())
    private val _resorts = MutableLiveData(initializeSkiResorts())
    val resorts: LiveData<List<SkiResort>> = _resorts

    private val _loaded = MutableLiveData(false)
    val loaded: LiveData<Boolean> = _loaded

    fun loadCurrentWeatherReport() {
        viewModelScope.launch {
            _resorts.value?.forEach { skiResort ->
                val result = currentForecastRepository.loadForecast(skiResort.latitude.toString(), skiResort.longitude.toString())
                skiResort.forecast = result.getOrDefault(CurrentWeatherReport(Forecast(), listOf<Forecast>())).periods
            }
            _resorts.value?.forEach { skiResort ->
                val result = historicalForecastRepository.loadForecast(skiResort.latitude.toString(), skiResort.longitude.toString())
                val hourlySnowfall = result.getOrDefault(HistoricalSnowfall(listOf<Snowfall>())).hourlySnowfall
                val totalSnowfall = hourlySnowfall.sumOf { snowfall -> snowfall.amount }
                skiResort.snowfall = String.format("%.2f",totalSnowfall)
            }
            _resorts.value?.forEach { skiResort ->
                val result = weatherAlertRepository.loadAlert(skiResort.latitude.toString() + "," + skiResort.longitude.toString())
                skiResort.alerts = result.getOrDefault(WeatherAlertReport(listOf<WeatherAlert>())).alerts
            }
            _loaded.value = true
        }
    }
}
