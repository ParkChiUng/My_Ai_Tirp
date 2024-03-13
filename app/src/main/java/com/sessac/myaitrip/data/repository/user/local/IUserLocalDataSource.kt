package com.sessac.myaitrip.data.repository.user.local

import com.gun0912.tedpermission.TedPermissionResult
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.Flow

interface IUserLocalDataSource {
    // User Preference DataStore
    suspend fun getUserPreferences(): Flow<UserPreferencesData>

    suspend fun updatePreferenceAutoLogin(autoLogin: Boolean)
    suspend fun resetPreferenceAutoLogin()

    suspend fun updatePreferenceUserId(userId: String)
    suspend fun resetPreferenceUserId()

    suspend fun checkPermission(): UiState<TedPermissionResult>
}