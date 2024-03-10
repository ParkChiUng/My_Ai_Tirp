package com.sessac.myaitrip.data.repository.tour.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.sessac.myaitrip.common.DEFAULT_PAGE_NUMBER
import com.sessac.myaitrip.common.DEFAULT_TOTAL_COUNT
import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource.UserPreferenceKeys.KEY_PAGE_NUMBER
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource.UserPreferenceKeys.KEY_TOTAL_COUNT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class TourLocalDataSource(
    private val tourDataStore: DataStore<Preferences>
) : ITourLocalDataSource {
    private object UserPreferenceKeys {
        val KEY_TOTAL_COUNT = intPreferencesKey("TOTAL_COUNT")
        val KEY_PAGE_NUMBER = intPreferencesKey("PAGE_NUMBER")
    }

    override suspend fun getTourPreferences(): Flow<TourPreferencesData> = tourDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("Error reading preferences.", exception.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapTourPreferences(preferences)
        }

    private fun mapTourPreferences(preferences: Preferences): TourPreferencesData {
        val totalCount = preferences[KEY_TOTAL_COUNT] ?: DEFAULT_TOTAL_COUNT
        val pageNumber = preferences[KEY_PAGE_NUMBER] ?: DEFAULT_PAGE_NUMBER
        return TourPreferencesData(totalCount, pageNumber)
    }

    override suspend fun updatePreferences(totalCount: Int, pagerNumber: Int) {
        tourDataStore.edit { preferences ->
            preferences[KEY_TOTAL_COUNT] = totalCount
            preferences[KEY_PAGE_NUMBER] = pagerNumber
        }
    }
}