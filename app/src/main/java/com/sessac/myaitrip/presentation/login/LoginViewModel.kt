package com.sessac.myaitrip.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _userPreferenceStatus = MutableStateFlow<UiState<UserPreferencesData>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    private val _loginStatus = MutableStateFlow<UiState<AuthResult>>(UiState.Empty)
    val loginStatus get() = _loginStatus.asStateFlow()

    init {
        getUserPreferences()
    }

    private fun getUserPreferences() {
        viewModelScope.launch {
            userRepository.getUserPreferences().collectLatest { userPreference ->
                _userPreferenceStatus.value = UiState.Success(userPreference)
            }
        }
    }

    // userId 값 저장
    fun updateUserPreferenceUserId(userId: String) {
        viewModelScope.launch {
            userRepository.updatePreferenceUserId(userId)
        }
    }

    // 자동 로그인 값 저장
    fun updateUserPreferenceAutoLogin(autoLogin: Boolean) {
        viewModelScope.launch {
            userRepository.updatePreferenceAutoLogin(autoLogin)
        }
    }

    fun resetUserPreferenceId() {
        viewModelScope.launch {
            userRepository.resetPreferenceUserId()
        }
    }

    fun resetUserPreferenceAutoLogin() {
        viewModelScope.launch {
            userRepository.resetPreferenceAutoLogin()
        }
    }

    /**
     * Login
     * 로그인 시도
     * @param email
     * @param password
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            userRepository.login(email, password).collectLatest {
                _loginStatus.value = it
            }
        }
    }
}