package com.sessac.myaitrip.data.repository.user.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.sessac.myaitrip.data.UserPreferences
import com.sessac.myaitrip.data.repository.user.datastore.UserDataStoreRepository.PreferenceKeys.KEY_USER_ID
import com.sessac.myaitrip.data.repository.user.datastore.UserDataStoreRepository.PreferenceKeys.KEY_USER_AUTO_LOGIN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserDataStoreRepository(
    private val userDataStore: DataStore<Preferences>
): IUserDataStoreRepository {
    private object PreferenceKeys {
        // 자동 로그인 여부
        val KEY_USER_AUTO_LOGIN = booleanPreferencesKey("USER_AUTO_LOGIN")

        // 회원 ID
        val KEY_USER_ID = stringPreferencesKey("USER_ID")
    }

    companion object {
        const val DEFAULT_AUTO_LOGIN = false
        const val DEFAULT_USER_ID = ""
    }

    override suspend fun getUserPreferences(): Flow<UserPreferences> = userDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("Error reading preferences.", exception.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapUserPreferences(preferences)
        }

    private fun mapUserPreferences(preferences: Preferences): UserPreferences {
        val autoLogin = preferences[KEY_USER_AUTO_LOGIN] ?: DEFAULT_AUTO_LOGIN // 자동 로그인 여부
        val userId = preferences[KEY_USER_ID] ?: DEFAULT_USER_ID // 회원 ID
        return UserPreferences(userId, autoLogin)
    }

    // 자동 로그인 여부
    /**
     * Update preference auto login
     * 자동 로그인 값 변경
     * @param autoLogin
     */
    override suspend fun updatePreferenceAutoLogin(autoLogin: Boolean) {
        userDataStore.edit { preferences ->
            preferences[KEY_USER_AUTO_LOGIN] = autoLogin
        }

        // 변환 블록의 모든 코드는 단일 트랜잭션으로 취급됩니다.
        // 내부적으로 트랜잭션 작업은 Dispacter.IO로 이동하므로 edit() 함수를 호출할 때 함수가 suspend되도록 해야 합니다.
    }

    /**
     * Reset preference auto login
     * 자동 로그인 디폴트 값으로
     */
    override suspend fun resetPreferenceAutoLogin() {
        userDataStore.edit { preferences ->
            preferences[KEY_USER_AUTO_LOGIN] = DEFAULT_AUTO_LOGIN
        }
    }

    // 회원 ID
    /**
     * Update preference user id
     * 현재 회원 ID 값 변경
     * @param userId
     */
    override suspend fun updatePreferenceUserId(userId: String) {
        userDataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

    /**
     * Reset preference user id
     * 현재 회원 ID 값 디폴트 값으로
     */
    override suspend fun resetPreferenceUserId() {
        userDataStore.edit { preferences ->
            preferences[KEY_USER_ID] = DEFAULT_USER_ID
        }
    }

}