package com.sessac.myaitrip.data.repository.tour

import com.google.firebase.FirebaseException
import com.sessac.myaitrip.data.api.TourApiService
import com.sessac.myaitrip.data.database.TourDao
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
    private val tourDao: TourDao,
    private val tourApiService: TourApiService,
    private val tourLocalDataSource: TourLocalDataSource,
    private val tourRemoteDataSource: TourRemoteDataSource
) {

    /**
     * [Preference] 관광지 저장한 개수 조회
     */
    suspend fun getTourPreferences(): Flow<TourPreferencesData> =
        tourLocalDataSource.getTourPreferences()

    /**
     * [Preference] 관광지 저장한 개수 저장
     */
    suspend fun updateTourPreference(totalCount: Int, pageNumber: Int) {
        tourLocalDataSource.updatePreferences(totalCount, pageNumber)
    }

    /**
     * [Room DB] 관광지 리스트 저장
     */
    fun insertTourListToRoom(newTourItem: List<TourItem>) {
        tourDao.insertTour(newTourItem)
    }

    /**
     * [Room DB] contentId List로 관광지 리스트 조회
     */
    fun getTourList(contentIdList: List<String>) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourDao.getTourList(contentIdList)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [Room DB] 지역 명과 카테고리로 관광지 리스트 조회
     */
    fun getTourList(area: String, category: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourDao.getTourList(area, category)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [공공 API] 관광지 전체 리스트 조회
     */
    fun getTourFromAPI(pageNumber: Int, numOfRow: Int) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourApiService.getAllData(pageNo = pageNumber, numOfRows = numOfRow)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [공공 API] 1개의 관광지 상세 내용 조회
     */
    fun getTourDetailFromAPI(contentId: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourApiService.getDetailData(contentId = contentId)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [공공 API] 1개의 관광지 이미지 조회
     */
    fun getTourImageFromAPI(contentId: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourApiService.getImageData(contentId = contentId)))
    }.catch { exception -> if (exception is Exception) emit(UiState.Error(exception)) }

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

    /**
     * [FireBase] 조회된 관광지 counting 추가
     */
    suspend fun addCountingFromFireBase(contentId: String) {
        tourRemoteDataSource.addCountingFromFireBase(contentId)
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
