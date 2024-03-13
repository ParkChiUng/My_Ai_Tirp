package com.sessac.myaitrip.data.repository.tour.remote

import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourDetailItems
import com.sessac.myaitrip.data.entities.remote.TourImageItems
import com.sessac.myaitrip.data.entities.remote.TourItems
import com.sessac.myaitrip.presentation.common.UiState

interface ITourRemoteDataSource {
    suspend fun getTourFromAPI(tourItems: Body<TourItems>): UiState<Body<TourItems>>

    suspend fun getTourDetailFromAPI(tourItems: Body<TourDetailItems>): UiState<Body<TourDetailItems>>

    suspend fun getTourImageFromAPI(tourItems: Body<TourImageItems>): UiState<Body<TourImageItems>>

    suspend fun getPopularTourListFromFireBase(listType: String): UiState<Map<String, Any>>

    suspend fun getAreaRecommendTourListFromFireBase(listType: String, cityName: String): UiState<Map<String, Any>>
}