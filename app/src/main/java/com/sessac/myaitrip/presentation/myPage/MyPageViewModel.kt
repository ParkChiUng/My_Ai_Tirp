package com.sessac.myaitrip.presentation.myPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.repository.diary.DiaryRepository
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

class MyPageViewModel(
    private val tourRepository: TourRepository,
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {
    private val _userLikeStatus = MutableStateFlow<UiState<MutableList<String>>>(UiState.Empty)
    val userLikeStatus get() = _userLikeStatus.asStateFlow()

    private val _userPreferenceStatus =
        MutableStateFlow<UiState<UserPreferencesData>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    private val _userProfileStatus = MutableStateFlow<UiState<Map<String, Any>>>(UiState.Empty)
    val userProfileStatus get() = _userProfileStatus.asStateFlow()

    private val _diaryStatus = MutableStateFlow<UiState<MutableList<DiaryItem>>>(UiState.Empty)
    val diaryStatus get() = _diaryStatus.asStateFlow()

    private val _likeTourListStatus = MutableStateFlow<UiState<List<TourItem>>>(UiState.Empty)
    val likeTourListStatus get() = _likeTourListStatus.asStateFlow()

    private val dispatchers = CoroutineScope(Dispatchers.IO)

    /**
     * 유저 정보 조회
     */
    fun getUserPreferences() {
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
                _userLikeStatus.value = it
            }
        }
    }

    /**
     * 유저 정보 조회
     */
    fun getUserProfileFromFireBase(userId: String) {
        viewModelScope.launch {
            tourRepository.getUserProfileFromFireBase(userId).collectLatest {
                _userProfileStatus.value = it
            }
        }
    }

    /**
     * 유저 다이어리 조회
     */
    fun getDiaryFromFireBase(userId: String) {
        viewModelScope.launch {
            diaryRepository.getDiaryFromFireBase(userId).collectLatest {
                _diaryStatus.value = it
            }
        }
    }

    /**
     * 좋아요한 관광지 리스트 정보 조회
     */
    fun getLikeTourList(contentId: MutableList<String>) {
        viewModelScope.launch {
            async(dispatchers.coroutineContext) {
                tourRepository.getTourList(contentId).collectLatest {
                    _likeTourListStatus.value = it
                }
            }.await()
        }
    }
}