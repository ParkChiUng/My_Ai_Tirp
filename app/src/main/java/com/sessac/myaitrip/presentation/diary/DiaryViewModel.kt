package com.sessac.myaitrip.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.repository.diary.DiaryRepository
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DiaryViewModel(
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _userPreferenceStatus =
        MutableStateFlow<UiState<UserPreferencesData>>(UiState.Empty)
    val userPreferenceStatus get() = _userPreferenceStatus.asStateFlow()

    private val _fireBaseResult = MutableStateFlow<UiState<String>>(UiState.Empty)
    val fireBaseResult get() = _fireBaseResult.asStateFlow()

    fun getUserPreferences() {
        viewModelScope.launch {
            diaryRepository.getUserPreferences().collectLatest {
                _userPreferenceStatus.value = UiState.Success(it)
            }
        }
    }

    fun addDiaryFromFireBase(userId: String, diaryItem: DiaryItem) {
        viewModelScope.launch {
            diaryRepository.addDiaryFromFireBase(userId, diaryItem).collectLatest {
                _fireBaseResult.value = it
            }
        }
    }
}
