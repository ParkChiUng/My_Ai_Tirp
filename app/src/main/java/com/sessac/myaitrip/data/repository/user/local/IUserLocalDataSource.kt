package com.sessac.myaitrip.data.repository.user.local

import com.sessac.myaitrip.data.UserPreferences
import kotlinx.coroutines.flow.Flow

interface IUserLocalDataSource {
    // User Preference DataStore
    suspend fun getUserPreferences(): Flow<UserPreferences>

    suspend fun updatePreferenceAutoLogin(autoLogin: Boolean)
    suspend fun resetPreferenceAutoLogin()

    suspend fun updatePreferenceUserId(userId: String)
    suspend fun resetPreferenceUserId()
}