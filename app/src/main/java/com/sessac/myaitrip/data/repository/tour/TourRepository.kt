package com.sessac.myaitrip.data.repository.tour

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.FirebaseException
import com.sessac.myaitrip.common.DEFAULT_PAGING_SIZE
import com.sessac.myaitrip.common.DEFAULT_PREFETCH_DISTANCE
import com.sessac.myaitrip.data.api.TourApiService
import com.sessac.myaitrip.data.database.TourDao
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource
import com.sessac.myaitrip.data.repository.tour.remote.TourRemoteDataSource
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.tours.source.TourPagingSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class TourRepository(
    private val tourDao: TourDao,
    private val tourApiService: TourApiService,
    private val tourLocalDataSource: TourLocalDataSource,
    private val tourRemoteDataSource: TourRemoteDataSource
) {
    /**
     * [API] 현재 위도, 경도 근처 20km 내에 있는 지역의 관광지 목록을 가져온다.
     * @param latitude
     * @param longitude
     */
    fun getAreaTourList(latitude: String, longitude: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourApiService.getLocationBasedData(latitude = latitude, longitude = longitude, radius = "20000")))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [API] 현재 위도, 경도 근처 3km 나의 근처에 있는 관광지 목록을 가져온다.
     * @param latitude
     * @param longitude
     */
    suspend fun getNearbyTourList(latitude: String, longitude: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourApiService.getLocationBasedData(latitude = latitude, longitude = longitude, radius = "3000")))
    }.catch { exception -> emit(UiState.Error(exception = exception, errorMessage = exception.localizedMessage)) }

    /**
     * Get around place list
     * 홈 화면 내 주변 추천 관광지 가져오기
     * 내 주변 3km 관광지 조회수 높은 순으로 가져오기
     * @param latitude
     * @param longitude
     */
    fun getRecommendAroundTourList(latitude: String, longitude: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourRemoteDataSource.getRecommendAroundTourList(latitude, longitude)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [Preference] 관광지 저장한 개수 조회
     */
    fun getTourPreferences() = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourLocalDataSource.getTourPreferences().first()))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

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
     * [Room DB] keyword를 포함하는 관광지 리스트 조회
     */
    fun getAIRecommendTourList(prompt: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourLocalDataSource.getAIRecommendMap(prompt)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [Room DB] contentId List로 관광지 리스트 조회
     */
    fun getTourList(contentIdList: List<String>) = flow {
//        emit(UiState.Loading)
        emit(UiState.Success(tourDao.getTourList(contentIdList)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [Room DB] contentId 1개의 관광지 리스트 조회
     */
    fun getTourList(contentIdList: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourDao.getTourList(contentIdList)))
    }.catch { exception -> UiState.Error(exception, errorMessage = exception.localizedMessage) }

    /**
     * [Room DB] 지역 명, 카테고리, 검색어로 관광지 리스트 조회 ( 페이징 )
     *
     * @param area 지역명
     * @param category 카테고리
     * @param inputText 검색어
     *
     * 검색어가 없다면 inputText == null
     */
    fun getTourList(area: String, category: String, inputText: String): Flow<PagingData<TourItem>> {
        return Pager(
            config = PagingConfig(pageSize = DEFAULT_PAGING_SIZE, prefetchDistance = DEFAULT_PREFETCH_DISTANCE),
            pagingSourceFactory = { TourPagingSource(tourDao = tourDao, area = area, category = category, inputText = inputText) }
        ).flow
    }

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
//        emit(UiState.Loading)
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

    /**
     * [FireBase] 관광지 조회수
     * @param contentId
     */
    suspend fun getTourViewCount(contentId: String) = flow {
        emit(UiState.Loading)
        emit(UiState.Success(tourRemoteDataSource.getTourViewCount(contentId)))
    }.catch { exception -> emit(UiState.Error(exception, errorMessage = exception.localizedMessage)) }

    /**
     * [FireBase] 유저 좋아요한 관광지 추가
     */
    suspend fun updateUserLikeListFromFireBase(userId: String, contentId: String) {
        tourRemoteDataSource.updateUserLikeFromFireBase(userId, contentId)
    }

    /**
     * [FireBase] 유저 좋아요 관광지 리스트 조회
     */
    fun getUserLikeListFromFireBase(userId: String) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(tourRemoteDataSource.getUserLikeFromFireBase(userId))
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
