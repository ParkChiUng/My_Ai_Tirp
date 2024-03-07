package com.sessac.myaitrip.presentation.register

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.QuerySnapshot
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _registerStatus = MutableStateFlow<UiState<AuthResult>>(UiState.Empty)
    val registerStatus get() = _registerStatus

//    private val _checkNickStatus = MutableStateFlow<UiState<QuerySnapshot>>(UiState.Empty)
//    val checkNickStatus get() = _checkNickStatus

    /**
     * Register
     * 회원가입
     * @param userEmail
     * @param userPassword
     * @param nickname
     * @param userProfileImageURL 소셜 로그인 기존 URL
     * @param userProfileImageURI 갤러리에서 가져온 새로운 이미지 URI
     */
    fun register(userEmail: String, userPassword: String, nickname: String, userProfileImageURL: String?, userProfileImageURI: Uri?) {
        viewModelScope.launch {
            userRepository.register(userEmail, userPassword, nickname, userProfileImageURL, userProfileImageURI).collectLatest {
                _registerStatus.value = it
            }
        }
    }

    /**
     * Check exist nickname
     * 닉네임 중복확인
     * @param nickname
     */
    /*fun checkExistNickname(nickname: String) {
        viewModelScope.launch {
            userRepository.checkExistNickname(nickname).collectLatest {
                _checkNickStatus.value = it
            }
        }
    }*/

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
}
