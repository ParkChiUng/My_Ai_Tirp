package com.sessac.myaitrip.data.repository.tour.remote

import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourItems
import com.sessac.myaitrip.presentation.common.UiState

class TourRemoteDataSource : ITourRemoteDataSource {
    override suspend fun getTourFromAPI(tourItems: Body<TourItems>): UiState<Body<TourItems>> {
        return UiState.Success(tourItems)
    }
}