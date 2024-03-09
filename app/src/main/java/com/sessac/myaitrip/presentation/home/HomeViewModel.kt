package com.sessac.myaitrip.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.repository.tour.TourRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val tourRepository = TourRepository.getInstance()

    private val _tourList = MutableStateFlow<List<TourItem>>(emptyList())
    val tourList: StateFlow<List<TourItem>> = _tourList

    private val _popularTourList = MutableStateFlow<List<TourItem>>(emptyList())
    val popularTourList: StateFlow<List<TourItem>> = _popularTourList

    private val _areaRecommendTourList = MutableStateFlow<List<TourItem>>(emptyList())
    val areaRecommendTourList: StateFlow<List<TourItem>> = _areaRecommendTourList

    private val _nearbyTourList = MutableStateFlow<List<TourItem>>(emptyList())
    val nearbyTourList: StateFlow<List<TourItem>> = _nearbyTourList

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    suspend fun geminiApi(prompt: String){

        try {
            val response = MyAiTripApplication.getGeminiModel().generateContent(prompt)

            // 숫자와 점 제거
            val cleanedInput = response.text!!.replace(Regex("\\d+\\."), "").trim()

            // 줄 바꿈으로 분리
            val tourList = cleanedInput.split("\n").map { it.trim() }

            viewModelScope.launch {
                tourRepository.getTourListByTitle(tourList).collect { tourList ->
                    _tourList.value = tourList
                }
            }
        }catch (e: Exception){
            Log.e("Gemini API Error", "An error occurred: ${e.message}")
        }
    }

    fun getTourListByContentId(contentIdList: List<String>, listType: ListType){
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

    enum class ListType {
        POPULAR,
        AREA_RECOMMEND,
        NEARBY
    }
}