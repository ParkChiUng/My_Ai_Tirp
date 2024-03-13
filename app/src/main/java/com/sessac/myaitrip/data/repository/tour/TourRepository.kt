package com.sessac.myaitrip.data.repository.tour

import android.util.Log
import com.google.firebase.FirebaseException
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.common.TOUR_API_SUCCESS_CODE
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource
import com.sessac.myaitrip.data.repository.tour.remote.TourRemoteDataSource
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class TourRepository(
    private val tourLocalDataSource: TourLocalDataSource,
    private val tourRemoteDataSource: TourRemoteDataSource
) {
    private val tourDao = MyAiTripApplication.getRoomDatabase().tourDao()
    private val tourApiService = MyAiTripApplication.getTourApiService()

    /**
     * [Room DB]관광지 리스트 저장
     */
    fun insertTourListToRoom(newTourItem: List<TourItem>) {
        tourDao.insertTour(newTourItem)
    }

    /**
     * [Room DB]contentId List로 관광지 리스트 조회
     */
    fun getTourListById(contentIdList: List<String>): Flow<List<TourItem>> {
        return tourDao.getTourListById(contentIdList)
    }

    /**
     * [Preference]관광지 저장한 개수 조회
     */
    suspend fun getTourPreferences(): Flow<TourPreferencesData> =
        tourLocalDataSource.getTourPreferences()

    /**
     * [Preference]관광지 저장한 개수 저장
     */
    suspend fun updateTourPreference(totalCount: Int, pageNumber: Int) {
        tourLocalDataSource.updatePreferences(totalCount, pageNumber)
    }

    /**
     * [공공 API] 관광지 전체 리스트 조회
     */
    fun getTourFromAPI(pageNumber: Int, numOfRow: Int) = flow {
        emit(UiState.Loading)
        val response = tourApiService.getAllData(pageNumber, numOfRow)
        if (response.response.header.resultCode == TOUR_API_SUCCESS_CODE) {
            emit(tourRemoteDataSource.getTourFromAPI(response.response.body))
        } else {
            Log.d("api error", "api error 1 ${response.response.header.resultMsg}")
            emit(UiState.Error(errorMessage = response.response.header.resultMsg))
        }
    }.catch {
        exception -> if (exception is Exception) emit(UiState.Error(exception)) }

    /**
     * [공공 API] 1개의 관광지 상세 내용 조회
     */
    fun getTourDetailFromAPI(contentId: String) = flow {
        emit(UiState.Loading)
        val response = tourApiService.getDetailData(contentId)
        if (response.response.header.resultCode == TOUR_API_SUCCESS_CODE) {
            emit(tourRemoteDataSource.getTourDetailFromAPI(response.response.body))
        } else {
            Log.d("api error", "api error 2 ${response.response.header.resultMsg}")
            emit(UiState.Error(errorMessage = response.response.header.resultMsg))
        }
    }.catch {
        exception -> if (exception is Exception) emit(UiState.Error(exception)) }

    /**
     * [공공 API] 1개의 관광지 이미지 조회
     */
    fun getTourImageFromAPI(contentId: String) = flow {
        emit(UiState.Loading)
        val response = tourApiService.getImageData(contentId)
        if (response.response.header.resultCode == TOUR_API_SUCCESS_CODE) {
            emit(tourRemoteDataSource.getTourImageFromAPI(response.response.body))
        } else {
            emit(UiState.Error(errorMessage = response.response.header.resultMsg))
        }
    }.catch { exception ->
        Log.d("test", "test : ${exception.message}")
        if (exception is Exception)
            emit(UiState.Error(exception))
    }

    /**
     * [FireBase] 인기 관광지 리스트 조회
     */
    fun getPopularTourListFromFireBase(listType: String) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(tourRemoteDataSource.getPopularTourListFromFireBase(listType))
    }.catch { exception ->
        if (exception is FirebaseException) emit(UiState.FirebaseApiError(exception))
    }

    /**
     * [FireBase] 지역 별 추천 관광지 리스트 조회
     */
    fun getAreaRecommendTourListFromFireBase(listType: String, cityName: String) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(tourRemoteDataSource.getAreaRecommendTourListFromFireBase(listType, cityName))
    }.catch { exception ->
        if (exception is FirebaseException) emit(UiState.FirebaseApiError(exception))
    }


    //    suspend fun getTourListByTitle(titles: List<String>): Flow<List<TourItem>> {
//        val result = mutableListOf<String>()
//        for (title in titles) {
//            val tourList = tourDao.getContentIdByTitle(title).first()
//            if (tourList.isNotEmpty())
//                result.add(tourList.first())
//        }
//        return tourDao.getTourListById(result)
//    }
}
