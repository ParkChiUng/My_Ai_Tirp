package com.sessac.myaitrip.presentation.tours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.repository.tour.TourRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToursViewModel(
    private val tourRepository: TourRepository
) : ViewModel() {

    private val _tourStatus =
        MutableStateFlow<PagingData<TourItem>>(PagingData.empty())
    val tourStatus get() = _tourStatus.asStateFlow()

    fun getTourList(area: String, category: String, inputText: String) {
        viewModelScope.launch {
            tourRepository.getTourList(area, category, inputText).cachedIn(viewModelScope).collectLatest { pagingData ->
                    _tourStatus.value = pagingData
                }
        }
    }
}
