package com.sessac.myaitrip.data.repository.user.datastore

import com.sessac.myaitrip.data.UserPreferences
import kotlinx.coroutines.flow.Flow

interface IUserDataStoreRepository {
    suspend fun getUserPreferences(): Flow<UserPreferences>

    suspend fun updatePreferenceAutoLogin(autoLogin: Boolean)
    suspend fun resetPreferenceAutoLogin()

    suspend fun updatePreferenceUserId(userId: String)
    suspend fun resetPreferenceUserId()
}