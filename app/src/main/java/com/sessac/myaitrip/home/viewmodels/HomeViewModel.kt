package com.sessac.myaitrip.home.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.tour.TourItem
import com.sessac.myaitrip.data.tour.repository.TourRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel(){

    private val tourRepository = TourRepository.getInstance()

    private val _tourList = MutableStateFlow<List<TourItem>>(emptyList())
    val tourList: StateFlow<List<TourItem>> = _tourList

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    suspend fun geminiApi(prompt: String){

        val response = MyAiTripApplication.getGeminiModel().generateContent(prompt)

        Log.d("test", "gemini response : ${response.text}")

        // 숫자와 점 제거
        val cleanedInput = response.text!!.replace(Regex("\\d+\\."), "").trim()

        // 줄바꿈으로 분리
        val tourList = cleanedInput.split("\n").map { it.trim() }

        Log.d("test", "gemini response : $tourList")

        viewModelScope.launch {
            tourRepository.getTourListByTitle(tourList).collect { tourList ->
                _tourList.value = tourList
            }
        }
    }

    fun getTourListById(contentId: List<String>){
        viewModelScope.launch {
            tourRepository.getTourListById(contentId).collect { tourList ->
                _tourList.value = tourList
            }
        }
    }
}