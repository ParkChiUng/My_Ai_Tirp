package com.sessac.myaitrip.data

sealed class UiState<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : UiState<T>(data)
    class Error<T>(message: String, data: T? = null) : UiState<T>(data, message)
    class Loading<T>(data: T? = null) : UiState<T>(data)
}