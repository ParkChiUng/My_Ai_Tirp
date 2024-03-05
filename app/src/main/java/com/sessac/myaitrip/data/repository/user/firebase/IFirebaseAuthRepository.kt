package com.sessac.myaitrip.data.repository.user.firebase

import com.google.firebase.auth.AuthResult
import com.sessac.myaitrip.data.UiState

interface IFirebaseAuthRepository {
    suspend fun register(email: String, nickname: String, password: String): UiState<AuthResult>
    suspend fun login(email: String, password: String): UiState<AuthResult>
}