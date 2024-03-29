package com.sessac.myaitrip.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.entities.remote.LocationBasedTourItem
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository
) : ViewModel() {

//    private val _tourList = MutableStateFlow<List<TourItem>>(emptyList())
//    val tourList: StateFlow<List<TourItem>> = _tourList

    private val _popularTourList =
        MutableStateFlow<UiState<List<TourItem>>>(UiState.Empty)
    val popularTourList get() = _popularTourList.asStateFlow()

    private val _areaRecommendTourList =
        MutableStateFlow<UiState<List<TourItem>>>(UiState.Empty)
    val areaRecommendTourList get() = _areaRecommendTourList.asStateFlow()

    private val _nearbyTourList = MutableStateFlow<UiState<List<LocationBasedTourItem>?>>(UiState.Empty)
    val nearbyTourList get() = _nearbyTourList.asStateFlow()

    private val _fireBaseResult = MutableStateFlow<UiState<Map<String, Any>>>(UiState.Empty)
    val fireBaseResult get() = _fireBaseResult.asStateFlow()

    private val _userLikeResult = MutableStateFlow<UiState<MutableList<String>>>(UiState.Empty)
    val userLikeResult get() = _userLikeResult.asStateFlow()

    private val _userPreferenceStatus = MutableStateFlow<UiState<UserPreferencesData>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    /**
     * Room에서 contentId로 관광지 조회
     * listType로 리스트 구분
     *
     * @param contentIdList
     * @param listType
     */
    fun getTourListByContentId(contentIdList: List<String>, listType: ListType) {
        viewModelScope.launch {
            async(dispatchers.coroutineContext) {
                tourRepository.getTourList(contentIdList).collectLatest { tourList ->
                    when (listType) {
                        ListType.POPULAR -> _popularTourList.value = tourList
                        ListType.AREA_RECOMMEND -> _areaRecommendTourList.value = tourList
                    }
                }
            }.await()
        }
    }

    fun getRecommendAroundTourList(latitude: String, longitude: String) {
        viewModelScope.launch {
            tourRepository.getRecommendAroundTourList(latitude, longitude).collectLatest {
                _nearbyTourList.value = it
            }
        }
    }

    /**
     *  fireBase에서 인기 관광지 조회
     *
     *  @param listType
     */
    fun getPopularTourListFromFireBase(listType: String) {
        viewModelScope.launch {
            tourRepository.getPopularTourListFromFireBase(listType).collectLatest {
                _fireBaseResult.value = it
            }
        }
    }

    /**
     * fireBase에서 지역 별 추천 관광지 조회
     *
     * @param listType
     * @param cityName
     */
    fun getAreaRecommendTourListFromFireBase(listType: String, cityName: String) {
        viewModelScope.launch {
            tourRepository.getAreaRecommendTourListFromFireBase(listType, cityName).collectLatest {
                _fireBaseResult.value = it
            }
        }
    }

    /**
     * 유저 정보 조회
     */
    fun getUserPreferences(){
        viewModelScope.launch {
            userRepository.getUserPreferences().collectLatest { userPreference ->
                _userPreferenceStatus.value = UiState.Success(userPreference)
            }
        }
    }

    /**
     * 좋아요 업데이트
     */
    fun updateUserLikeListFromFireBase(userId: String, contentId: String) {
        viewModelScope.launch {
            tourRepository.updateUserLikeListFromFireBase(userId, contentId)
        }
    }

    /**
     * 유저 좋아요 리스트 조회
     */
    fun getUserLikeListFromFireBase(userId: String) {
        viewModelScope.launch {
            tourRepository.getUserLikeListFromFireBase(userId).collectLatest {
                _userLikeResult.value = it
            }
        }
    }

    /**
     * 리스트 타입
     */
    enum class ListType {
        POPULAR,
        AREA_RECOMMEND
    }

    //    suspend fun geminiApi(prompt: String){
//
//        try {
//            val response = MyAiTripApplication.getGeminiModel().generateContent(prompt)
//
//            // 숫자와 점 제거
//            val cleanedInput = response.text!!.replace(Regex("\\d+\\."), "").trim()
//
//            // 줄 바꿈으로 분리
//            val tourList = cleanedInput.split("\n").map { it.trim() }
//
//            viewModelScope.launch {
//                tourRepository.getTourListByTitle(tourList).collect { tourList ->
//                    _tourList.value = tourList
//                }
//            }
//        }catch (e: Exception){
//            Log.e("Gemini API Error", "An error occurred: ${e.message}")
//        }
//    }
}