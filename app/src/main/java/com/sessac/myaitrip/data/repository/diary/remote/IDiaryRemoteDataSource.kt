package com.sessac.myaitrip.data.repository.diary.remote

import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.presentation.common.UiState

interface IDiaryRemoteDataSource {
    suspend fun addDiaryFromFireBase(userId: String, diaryItem: DiaryItem): UiState<String>

    suspend fun getDiaryFromFireBase(userId: String): UiState<MutableList<DiaryItem>>
}