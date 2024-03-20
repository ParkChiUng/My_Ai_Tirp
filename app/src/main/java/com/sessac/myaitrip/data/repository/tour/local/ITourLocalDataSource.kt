package com.sessac.myaitrip.data.repository.tour.local

import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import kotlinx.coroutines.flow.Flow

interface ITourLocalDataSource {
    suspend fun getTourPreferences(): Flow<TourPreferencesData>
    suspend fun updatePreferences(totalCount: Int, pagerNumber: Int)
}