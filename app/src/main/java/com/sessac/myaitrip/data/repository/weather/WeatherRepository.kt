package com.sessac.myaitrip.data.repository.weather

import com.sessac.myaitrip.data.api.WeatherApiService
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val weatherApiService: WeatherApiService
) {
    suspend fun getWeatherData(todayDate: String, latitude: String, longitude: String ) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(weatherApiService.getWeatherData(baseDate = todayDate, nx = latitude, ny = longitude)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }
}