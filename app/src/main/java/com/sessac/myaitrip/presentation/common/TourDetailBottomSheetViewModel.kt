package com.sessac.myaitrip.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.repository.tour.TourRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TourDetailBottomSheetViewModel(
    private val tourRepository: TourRepository
): ViewModel() {

    private val _viewCountStatus = MutableStateFlow<UiState<Long>>(UiState.Empty)
    val viewCountStatus get() = _viewCountStatus.asStateFlow()

    fun getTourViewCount(contentId: String) {
        viewModelScope.launch {
            tourRepository.getTourViewCount(contentId).collectLatest {
                _viewCountStatus.value = it
            }
        }
    }
}