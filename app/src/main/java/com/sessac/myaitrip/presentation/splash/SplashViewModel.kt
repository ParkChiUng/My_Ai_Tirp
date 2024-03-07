package com.sessac.myaitrip.presentation.splash

import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.data.UserPreferences
import com.sessac.myaitrip.data.repository.user.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userRepository: UserRepository
): ViewModel() {
    private val _userPreferenceStatus = MutableStateFlow<UiState<UserPreferences>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    fun getUserPreferences() {
        viewModelScope.launch {
            userRepository.getUserPreferences().collectLatest {
                _userPreferenceStatus.value = UiState.Success(it)
            }
        }
    }
}