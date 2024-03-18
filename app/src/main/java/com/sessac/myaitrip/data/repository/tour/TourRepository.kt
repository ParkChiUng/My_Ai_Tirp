package com.sessac.myaitrip.data.repository.tour

import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.api.TourApiService
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.network.RetrofitServiceInstance
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TourRepository(
  private val tourApiService: TourApiService
) {

    private val tourDao = MyAiTripApplication.getRoomDatabase().tourDao()

    fun insertTour(newTourItem: List<TourItem>) {
        tourDao.insertTour(newTourItem)
    }

    suspend fun getTourListByTitle(titles: List<String>): Flow<List<TourItem>> {
        val result = mutableListOf<String>()
        for (title in titles) {
            val tourList = tourDao.getContentIdByTitle(title).first()
            if(tourList.isNotEmpty())
                result.add(tourList.first())
        }
        return tourDao.getTourListById(result)
    }

    fun getTourListById(contentIdList: List<String>): Flow<List<TourItem>>{
        return tourDao.getTourListById(contentIdList)
    }

    suspend fun getPlaceListByLocation(latitude: String, longitude: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourApiService.getLocationBasedData(latitude = latitude, longitude = longitude)))
    }.catch { exception -> emit(UiState.Error(exception = exception, errorMessage = exception.localizedMessage)) }

    companion object {
        private var instance: TourRepository? = null

        fun getInstance(): TourRepository {
            if (instance == null) {
                instance = TourRepository(RetrofitServiceInstance.getTourApiService())
            }
            return instance!!
        }
    }
}
