package com.sessac.myaitrip.data.repository.diary

import com.google.firebase.FirebaseException
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.data.entities.local.UserPreferencesData
import com.sessac.myaitrip.data.repository.diary.remote.DiaryRemoteDataSource
import com.sessac.myaitrip.data.repository.user.local.UserLocalDataSource
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DiaryRepository(
    private val userLocalDataSource: UserLocalDataSource,
    private val diaryRemoteDataSource: DiaryRemoteDataSource
) {

    /**
     * [Preference]유저 정보 조회
     */
    suspend fun getUserPreferences(): Flow<UserPreferencesData> =
        userLocalDataSource.getUserPreferences()


    /**
     * [FireBase] 다이어리 저장
     */
    fun addDiaryFromFireBase(userId: String, diaryItem: DiaryItem) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(diaryRemoteDataSource.addDiaryFromFireBase(userId, diaryItem))
    }.catch { exception ->
        if (exception is FirebaseException) emit(UiState.FirebaseApiError(exception))
    }

    fun getDiaryFromFireBase(userId: String) = flow {
        emit(UiState.Loading)
        delay(300)
        emit(diaryRemoteDataSource.getDiaryFromFireBase(userId))
    }.catch { exception ->
        if (exception is FirebaseException) emit(UiState.FirebaseApiError(exception))
    }
}
