package com.sessac.myaitrip.data.repository.user.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.QuerySnapshot
import com.sessac.myaitrip.presentation.common.UiState

interface IUserRemoteDataSource {

    // Firebase Auth
    suspend fun register(email: String, nickname: String, password: String): UiState<AuthResult>
    suspend fun login(email: String, password: String): UiState<AuthResult>

    suspend fun checkExistNickname(nickname: String): UiState<Boolean>
}