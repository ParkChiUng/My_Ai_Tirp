package com.sessac.myaitrip.presentation.tourmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.remote.ApiResponse
import com.sessac.myaitrip.data.entities.remote.WeatherApiResponse
import com.sessac.myaitrip.data.entities.remote.WeatherItems
import com.sessac.myaitrip.data.repository.weather.WeatherRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TourMapViewModel(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private val _weatherStatus = MutableStateFlow<UiState<WeatherApiResponse<WeatherItems>>>(UiState.Empty)
    val weatherStatus get() = _weatherStatus

    fun getWeatherData(todayDate: String, latitude: String, longitude: String) {
        viewModelScope.launch {
            weatherRepository.getWeatherData(todayDate, latitude, longitude).collectLatest {
                _weatherStatus.value = it
            }
        }
    }
}