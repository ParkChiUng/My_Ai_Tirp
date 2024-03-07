package com.sessac.myaitrip.data.repository.user

import android.net.Uri
import com.google.firebase.auth.FirebaseAuthException
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.data.UserPreferences
import com.sessac.myaitrip.data.repository.user.local.UserLocalDataSource
import com.sessac.myaitrip.data.repository.user.remote.UserRemoteDataSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UserRepository(
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource
) {
    suspend fun getUserPreferences(): Flow<UserPreferences> = userLocalDataSource.getUserPreferences()

    suspend fun updatePreferenceAutoLogin(autoLogin: Boolean) {
        userLocalDataSource.updatePreferenceAutoLogin(autoLogin)
    }

    suspend fun resetPreferenceAutoLogin() {
        userLocalDataSource.resetPreferenceAutoLogin()
    }

    suspend fun updatePreferenceUserId(userId: String) {
        userLocalDataSource.updatePreferenceUserId(userId)
    }

    suspend fun resetPreferenceUserId() {
        userLocalDataSource.resetPreferenceUserId()
    }

    // Firebase Auth

    fun login(email: String, password: String) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(userRemoteDataSource.login(email, password))
    }.catch { exception -> if(exception is FirebaseAuthException) emit(UiState.FirebaseAuthError(exception))}

    fun register(
        email: String,
        password: String,
        nickname: String,
        profileImgUrl: String?,
        newProfileImgUri: Uri?
    ) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(userRemoteDataSource.register(email, password, nickname, profileImgUrl, newProfileImgUri))
    }.catch { exception -> emit(UiState.Error(exception, errorMessage = exception.localizedMessage)) }

    /*fun checkExistNickname(nickname: String) = flow {
        emit(UiState.Loading)
        emit(userRemoteDataSource.checkExistNickname(nickname))
    }.catch { exception -> emit(UiState.Error(exception, errorMessage = exception.localizedMessage)) }*/
}