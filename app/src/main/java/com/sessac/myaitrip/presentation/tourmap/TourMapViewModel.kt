package com.sessac.myaitrip.presentation.tourmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.remote.ApiResponse
import com.sessac.myaitrip.data.entities.remote.LocationBasedTourItems
import com.sessac.myaitrip.data.entities.remote.WeatherApiResponse
import com.sessac.myaitrip.data.entities.remote.WeatherItems
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.data.repository.weather.WeatherRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TourMapViewModel(
    private val weatherRepository: WeatherRepository,
    private val tourRepository: TourRepository
): ViewModel() {

    private val _weatherStatus = MutableStateFlow<UiState<WeatherApiResponse<WeatherItems>>>(UiState.Empty)
    val weatherStatus get() = _weatherStatus.asStateFlow()

    private val _nearbyTourStatus = MutableStateFlow<UiState<ApiResponse<LocationBasedTourItems>>>(UiState.Empty)
    val nearbyTourStatus get() = _nearbyTourStatus.asStateFlow()

    private val _areaTourStatus = MutableStateFlow<UiState<ApiResponse<LocationBasedTourItems>>>(UiState.Empty)
    val areaTourStatus get() = _areaTourStatus.asStateFlow()

    fun getWeatherData(date: String, pointX: String, pointY: String) {
        viewModelScope.launch {
            weatherRepository.getWeatherData(date = date, pointX = pointX, pointY = pointY).collectLatest {
                _weatherStatus.value = it
            }
        }
    }

    fun getAreaTourList(latitude: String, longitude: String) {
        viewModelScope.launch {
            tourRepository.getAreaTourList(latitude, longitude).collectLatest {
                _areaTourStatus.value = it
            }
        }
    }

    fun getNearbyTourList(latitude: String, longitude: String) {
        viewModelScope.launch {
            tourRepository.getNearbyTourList(latitude, longitude).collectLatest {
                _nearbyTourStatus.value = it
            }
        }
    }
}