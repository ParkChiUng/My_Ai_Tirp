package com.sessac.myaitrip.data.repository.tour.remote

import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourDetailItems
import com.sessac.myaitrip.data.entities.remote.TourImageItems
import com.sessac.myaitrip.data.entities.remote.TourItems
import com.sessac.myaitrip.presentation.common.UiState

interface ITourRemoteDataSource {
    suspend fun getPopularTourListFromFireBase(listType: String): UiState<Map<String, Any>>

    suspend fun getAreaRecommendTourListFromFireBase(listType: String, cityName: String): UiState<Map<String, Any>>

    suspend fun addCountingFromFireBase(contentId: String)

    suspend fun getUserLikeFromFireBase(userId: String): UiState<List<String>>

    suspend fun updateUserLikeFromFireBase(userId: String, contentId: String)
}