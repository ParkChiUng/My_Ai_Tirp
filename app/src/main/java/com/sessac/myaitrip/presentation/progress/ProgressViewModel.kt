package com.sessac.myaitrip.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourItems
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProgressViewModel(
    private val tourRepository: TourRepository
) : ViewModel() {
    private val _tourPreferenceStatus =
        MutableStateFlow<UiState<TourPreferencesData>>(UiState.Empty)
    val tourPreferenceStatus get() = _tourPreferenceStatus.asStateFlow()

    private val _tourApiStatus = MutableStateFlow<UiState<Body<TourItems>>>(UiState.Empty)
    val tourApiStatus get() = _tourApiStatus.asStateFlow()

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    init {
        getTourPreferences()
    }

    private fun getTourPreferences() {
        viewModelScope.launch {
            tourRepository.getTourPreferences().collectLatest { tourPreference ->
                _tourPreferenceStatus.value = UiState.Success(tourPreference)
            }
        }
    }

    fun updateTourPreference(totalCount: Int, pageNumber: Int) {
        viewModelScope.launch {
            tourRepository.updateTourPreference(totalCount, pageNumber)
        }
    }

    fun insertTour(tourItem: List<TourItem>) {
        viewModelScope.launch {
            withContext(dispatchers.coroutineContext) {
                tourRepository.insertTourListToRoom(tourItem)
            }
        }
    }

    fun getTourFromAPI(pageNumber: Int, numOfRow: Int) {
        viewModelScope.launch {
            tourRepository.getTourFromAPI(pageNumber, numOfRow).collectLatest {
                _tourApiStatus.value = it
            }
        }
    }
}
