package com.sessac.myaitrip.data.repository.tour

import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.common.TOUR_API_SUCCESS_CODE
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource
import com.sessac.myaitrip.data.repository.tour.remote.TourRemoteDataSource
import com.sessac.myaitrip.presentation.common.ApiException
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TourRepository(
    private val tourLocalDataSource: TourLocalDataSource,
    private val tourRemoteDataSource: TourRemoteDataSource
) {
    private val tourDao = MyAiTripApplication.getRoomDatabase().tourDao()
    private val tourApiService = MyAiTripApplication.getTourApiService()

    fun insertTour(newTourItem: List<TourItem>) {
        tourDao.insertTour(newTourItem)
    }

    suspend fun getTourListByTitle(titles: List<String>): Flow<List<TourItem>> {
        val result = mutableListOf<String>()
        for (title in titles) {
            val tourList = tourDao.getContentIdByTitle(title).first()
            if (tourList.isNotEmpty())
                result.add(tourList.first())
        }
        return tourDao.getTourListById(result)
    }

    fun getTourListById(contentIdList: List<String>): Flow<List<TourItem>> {
        return tourDao.getTourListById(contentIdList)
    }

    suspend fun getTourPreferences(): Flow<TourPreferencesData> =
        tourLocalDataSource.getTourPreferences()

    suspend fun updateTourPreference(totalCount: Int, pageNumber: Int) {
        tourLocalDataSource.updatePreferences(totalCount, pageNumber)
    }

    fun getTourFromAPI(pageNumber: Int, numOfRow: Int) = flow {
        emit(UiState.Loading)
        val response = tourApiService.getAllData(pageNumber, numOfRow)
        if (response.response.header.resultCode == TOUR_API_SUCCESS_CODE) {
            emit(tourRemoteDataSource.getTourFromAPI(response.response.body))
        } else {
            emit(UiState.Error(errorMessage = response.response.header.resultMsg))
        }
    }.catch { exception -> if (exception is ApiException) emit(UiState.ApiError(exception)) }

}
