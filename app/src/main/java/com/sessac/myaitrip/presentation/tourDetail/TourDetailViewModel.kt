package com.sessac.myaitrip.presentation.tourDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.entities.remote.ApiResponse
import com.sessac.myaitrip.data.entities.remote.TourDetailItems
import com.sessac.myaitrip.data.entities.remote.TourImageItems
import com.sessac.myaitrip.data.entities.remote.TourItems
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

class TourDetailViewModel(
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _tourDetailStatus =
        MutableStateFlow<UiState<ApiResponse<TourDetailItems>>>(UiState.Empty)
    val tourDetailStatus get() = _tourDetailStatus.asStateFlow()

    private val _tourImgStatus =
        MutableStateFlow<UiState<ApiResponse<TourImageItems>>>(UiState.Empty)
    val tourImgStatus get() = _tourImgStatus.asStateFlow()

    private val _tourStatus =
        MutableStateFlow<UiState<TourItem>>(UiState.Empty)
    val tourStatus get() = _tourStatus.asStateFlow()

    private val _userLikeResult = MutableStateFlow<UiState<MutableList<String>>>(UiState.Empty)
    val userLikeResult get() = _userLikeResult.asStateFlow()

    private val _userPreferenceStatus = MutableStateFlow<UiState<UserPreferencesData>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    fun getTourFromRoom(contentId: String) {
        viewModelScope.launch {
            async(dispatchers.coroutineContext) {
                tourRepository.getTourList(contentId).collectLatest {
                    _tourStatus.value = it
                }
            }.await()
        }
    }

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

    fun addCountingFromFireBase(contentId: String) {
        viewModelScope.launch {
            tourRepository.addCountingFromFireBase(contentId)
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
}
