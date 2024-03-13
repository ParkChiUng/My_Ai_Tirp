package com.sessac.myaitrip.presentation.tourDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourDetailItems
import com.sessac.myaitrip.data.entities.remote.TourImageItems
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TourDetailViewModel(
    private val tourRepository: TourRepository
) : ViewModel() {
    private val _tourDetailStatus =
        MutableStateFlow<UiState<Body<TourDetailItems>>>(UiState.Empty)
    val tourDetailStatus get() =
        _tourDetailStatus.asStateFlow()

    private val _tourImgStatus =
        MutableStateFlow<UiState<Body<TourImageItems>>>(UiState.Empty)
    val tourImgStatus get() =
        _tourImgStatus.asStateFlow()


    fun getTourDetailFromAPI(contentId: String) {
        viewModelScope.launch {
            tourRepository.getTourDetailFromAPI(contentId).collectLatest {
                _tourDetailStatus.value = it
            }
        }
    }

    fun getTourImageFromAPI(contentId: String) {
        viewModelScope.launch {
            tourRepository.getTourImageFromAPI(contentId).collectLatest {
                _tourImgStatus.value = it
            }
        }
    }
}
