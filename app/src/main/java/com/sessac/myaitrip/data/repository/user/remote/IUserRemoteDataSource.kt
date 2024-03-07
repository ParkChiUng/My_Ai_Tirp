package com.sessac.myaitrip.data.repository.user.remote

import android.net.Uri
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.QuerySnapshot
import com.sessac.myaitrip.presentation.common.UiState

interface IUserRemoteDataSource {

    // Firebase Auth
    suspend fun register(email: String, password: String, nickname: String, profileImgUrl: String?, newProfileImgUri: Uri?): UiState<AuthResult>

    suspend fun login(email: String, password: String): UiState<AuthResult>

//    suspend fun checkExistNickname(nickname: String): UiState<QuerySnapshot>
}