package com.sessac.myaitrip.data.repository.tour.local

import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import kotlinx.coroutines.flow.Flow

interface ITourLocalDataSource {
    suspend fun getTourPreferences(): Flow<TourPreferencesData>
    suspend fun updatePreferences(totalCount: Int, pagerNumber: Int)

    suspend fun getAIRecommendMap(prompt: String): Map<String, List<TourItem>>
}