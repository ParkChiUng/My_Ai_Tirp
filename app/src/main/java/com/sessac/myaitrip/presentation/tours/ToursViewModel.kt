package com.sessac.myaitrip.presentation.tours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ToursViewModel(
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _tourStatus =
        MutableStateFlow<PagingData<TourItem>>(PagingData.empty())
    val tourStatus get() = _tourStatus.asStateFlow()

    private val _userLikeResult = MutableStateFlow<UiState<MutableList<String>>>(UiState.Empty)
    val userLikeResult get() = _userLikeResult.asStateFlow()

    private val _userPreferenceStatus = MutableStateFlow<UiState<UserPreferencesData>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    /**
     * 관광지 조회
     */
    fun getTourList(area: String, category: String, inputText: String) {
        viewModelScope.launch {
            tourRepository.getTourList(area, category, inputText).cachedIn(viewModelScope).collectLatest { pagingData ->
                    _tourStatus.value = pagingData
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
}
