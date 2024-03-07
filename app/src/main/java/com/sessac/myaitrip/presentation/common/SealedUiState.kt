package com.sessac.myaitrip.presentation.common

import com.google.firebase.auth.FirebaseAuthException

sealed class UiState<out T> {
    data object Empty: UiState<Nothing>()
    data object Loading: UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable? = null, val errorMessage: String? = null): UiState<Nothing>()
    data class FirebaseAuthError(val firebaseAuthException: FirebaseAuthException): UiState<Nothing>()
}