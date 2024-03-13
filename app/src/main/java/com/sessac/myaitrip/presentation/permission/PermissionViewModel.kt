package com.sessac.myaitrip.presentation.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gun0912.tedpermission.TedPermissionResult
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PermissionViewModel(
    private val userRepository: UserRepository
): ViewModel() {

    private val _checkPermissionStatus = MutableStateFlow<UiState<TedPermissionResult>>(UiState.Empty)
    val checkPermissionStatus get() = _checkPermissionStatus.asStateFlow()

    init {
        checkPermission()
    }

    private fun checkPermission() {
        viewModelScope.launch {
            userRepository.checkPermission().collectLatest {
                _checkPermissionStatus.value = it
            }
        }
    }
}