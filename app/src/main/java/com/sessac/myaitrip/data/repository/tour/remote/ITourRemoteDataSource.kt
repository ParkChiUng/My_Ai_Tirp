package com.sessac.myaitrip.data.repository.tour.remote

import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourItems
import com.sessac.myaitrip.presentation.common.UiState

interface ITourRemoteDataSource {
    suspend fun getTourFromAPI(tourItems: Body<TourItems>): UiState<Body<TourItems>>
}