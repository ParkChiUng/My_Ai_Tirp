package com.sessac.myaitrip.data.repository.diary.remote

import android.util.Log
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class DiaryRemoteDataSource : IDiaryRemoteDataSource {
    private val fireStore = MyAiTripApplication.getInstance().getFireStore()
    private val fireStorage = MyAiTripApplication.getInstance().getFireStorage()
    private var diaryItemUri: MutableList<String> = mutableListOf()
    private var timestamp: Long = 0
    private lateinit var userId: String
    private lateinit var diaryItem: DiaryItem

    override suspend fun addDiaryFromFireBase(
        userId: String,
        diaryItem: DiaryItem
    ): UiState<String> {
        timestamp = System.currentTimeMillis()

        this.userId = userId
        this.diaryItem = diaryItem

        return CoroutineScope(Dispatchers.IO).async {
            /**
             * 1. 이미지 fireStorage 저장
             * 2. 저장한 이미지 Uri 다운로드
             * 3. 다운로드한 Uri diaryItemUri 저장
             */
            diaryItem.diaryImage.forEachIndexed { index, uri ->
                val profileImgRef = fireStorage.reference.child("diaryImage").child(userId).child("$timestamp-$index.png")
                val uploadTask = profileImgRef.putFile(uri)

                uploadTask.addOnSuccessListener {
                    profileImgRef.downloadUrl.addOnSuccessListener {
                        Log.d("FirebaseStorage", "File download successfully")
                        diaryItemUri.add(it.toString())

                        if (index == diaryItem.diaryImage.size - 1)
                            addDiaryFromFireStore()
                    }
                    Log.d("FirebaseStorage", "File uploaded successfully")
                }.addOnFailureListener { e ->
                    Log.w("FirebaseStorage", "Failed to upload file", e)
                }
            }

            UiState.Success(userId)
        }.await()
    }

    /**
     * fireStore에 다이어리 정보 저장
     */
    private fun addDiaryFromFireStore() {
        val diaryData = hashMapOf(
            "diaryContentId" to diaryItem.contentId,
            "diaryTitle" to diaryItem.diaryTitle,
            "diaryReview" to diaryItem.diaryReview,
            "diaryImage" to diaryItemUri
        )

        val diaryFireStore = fireStore.collection("user")
            .document(userId)
            .collection("diary")
            .document(timestamp.toString())

        diaryFireStore.set(diaryData)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
            }
    }
}