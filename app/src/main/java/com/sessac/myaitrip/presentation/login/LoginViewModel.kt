package com.sessac.myaitrip.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.sessac.myaitrip.data.UiState
import com.sessac.myaitrip.data.UserPreferences
import com.sessac.myaitrip.data.repository.user.datastore.UserDataStoreRepository
import com.sessac.myaitrip.data.repository.user.firebase.FirebaseAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userDataStoreRepository: UserDataStoreRepository,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _userPreferenceStatus: MutableStateFlow<UserPreferences> = MutableStateFlow(
        UserPreferences("", false)
    )
    val userPreferenceStatus: StateFlow<UserPreferences>
        get() = _userPreferenceStatus.asStateFlow()

    private val _registerStatus: MutableStateFlow<UiState<AuthResult>> =
        MutableStateFlow(UiState.Loading(null))
    val registerStatus: StateFlow<UiState<AuthResult>>
        get() = _registerStatus.asStateFlow()

    private val _loginStatus: MutableStateFlow<UiState<AuthResult>> =
        MutableStateFlow(UiState.Loading(null))
    val loginStatus: StateFlow<UiState<AuthResult>>
        get() = _loginStatus.asStateFlow()

    init {
        getUserPreferences()
    }

    private fun getUserPreferences() {
        viewModelScope.launch {
            userDataStoreRepository.getUserPreferences().collect { userPreference ->
                _userPreferenceStatus.update {
                    it.copy(
                        userId =  userPreference.userId,
                        autoLogin = userPreference.autoLogin
                    )
                }
            }
        }
    }

    // userId 값 저장
    fun updateUserPreferenceUserId(userId: String) {
        viewModelScope.launch {
            userDataStoreRepository.updatePreferenceUserId(userId)
        }
    }

    // 자동 로그인 값 저장
    fun updateUserPreferenceAutoLogin(autoLogin: Boolean) {
        viewModelScope.launch {
            userDataStoreRepository.updatePreferenceAutoLogin(autoLogin)
        }
    }

    /**
     * Login
     * 로그인 시도
     * @param email
     * @param password
     */
    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginStatus.update { UiState.Error("값을 입력해주세요.") }
            return
        } else {
            _loginStatus.update { UiState.Loading() }
            viewModelScope.launch {
                val result = firebaseAuthRepository.login(email, password)
                _loginStatus.update { result }
            }
        }
    }
}