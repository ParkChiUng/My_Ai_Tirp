package com.sessac.myaitrip.presentation.airecommend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AIRecommendViewModel(
    private val tourRepository: TourRepository
) : ViewModel() {

    private val _aiResultStatus = MutableStateFlow<UiState<Map<String, List<TourItem>>>>(UiState.Empty)
    val aiResultStatus get() = _aiResultStatus.asStateFlow()

    fun getTourByGeminiAiWithPrompt(prompt: String) {
        viewModelScope.launch {
            tourRepository.getAIRecommendTourList(prompt).collectLatest {
                _aiResultStatus.value = it
            }
        }
    }
}