package com.sessac.myaitrip.data.repository.weather

import com.sessac.myaitrip.data.api.WeatherApiService
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class WeatherRepository(
    private val weatherApiService: WeatherApiService
) {
    /**
     * [API] 현재 위도, 경도를 X, Y좌표 변환 후, 현재 위치의 날씨 정보 가져오기
     * @param date
     * @param pointX
     * @param pointY
     */
    suspend fun getWeatherData(date: String, pointX: String, pointY: String ) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(weatherApiService.getWeatherData(baseDate = date, nx = pointX, ny = pointY)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }
}