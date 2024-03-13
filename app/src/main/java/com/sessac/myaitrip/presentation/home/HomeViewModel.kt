package com.sessac.myaitrip.presentation.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val tourRepository: TourRepository
) : ViewModel() {

//    private val _tourList = MutableStateFlow<List<TourItem>>(emptyList())
//    val tourList: StateFlow<List<TourItem>> = _tourList

    private val _popularTourList = MutableStateFlow<List<TourItem>>(emptyList())
    val popularTourList: StateFlow<List<TourItem>> = _popularTourList

    private val _fireBaseResult = MutableStateFlow<UiState<Map<String, Any>>>(UiState.Empty)
    val fireBaseResult get() = _fireBaseResult.asStateFlow()

    private val _areaRecommendTourList = MutableStateFlow<List<TourItem>>(emptyList())
    val areaRecommendTourList: StateFlow<List<TourItem>> = _areaRecommendTourList

    private val _nearbyTourList = MutableStateFlow<List<TourItem>>(emptyList())
    val nearbyTourList: StateFlow<List<TourItem>> = _nearbyTourList

    /**
     * Room에서 contentId로 관광지 조회
     * listType로 리스트 구분
     *
     * @param contentIdList
     * @param listType
     */
    fun getTourListByContentId(contentIdList: List<String>, listType: ListType) {
        viewModelScope.launch {
            tourRepository.getTourListById(contentIdList).collect { tourList ->
                when (listType) {
                    ListType.POPULAR -> _popularTourList.value = tourList
                    ListType.AREA_RECOMMEND -> _areaRecommendTourList.value = tourList
                    ListType.NEARBY -> _nearbyTourList.value = tourList
                }
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
     * 리스트 타입
     */
    enum class ListType {
        POPULAR,
        AREA_RECOMMEND,
        NEARBY
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