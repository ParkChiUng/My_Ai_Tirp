package com.sessac.myaitrip.data.repository.tour.remote

import android.util.Log
import com.sessac.myaitrip.common.CONTENT_ID_LIST
import com.sessac.myaitrip.common.LIST_TYPE
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.tasks.await

class TourRemoteDataSource : ITourRemoteDataSource {
    private val fireStore = MyAiTripApplication.getInstance().getFireStore()

    /**
     * 인기 관광지 조회
     */
    override suspend fun getPopularTourListFromFireBase(listType: String): UiState<Map<String, Any>> {
        return try {
            val resultList = mutableMapOf<String, Any>()

            val documents = fireStore.collection("tour").document("data").collection("popular")
                .get()
                .await()

            val contentIdList = documents.map { doc ->
                Pair(doc.id, doc.getLong("counting") ?: 0)
            }.sortedByDescending { it.second }
                .take(5)
                .map { it.first }

            resultList[CONTENT_ID_LIST] = contentIdList
            resultList[LIST_TYPE] = listType

            UiState.Success(resultList)
        } catch (exception: Exception) {
            Log.d("TAG", "get failed ", exception)
            UiState.Error(exception)
        }
    }

    /**
     * 추천 관광지 조회
     */
    override suspend fun getAreaRecommendTourListFromFireBase(
        listType: String,
        cityName: String
    ): UiState<Map<String, Any>> {
        return try {
            val resultList = mutableMapOf<String, Any>()

            val document = fireStore.collection("tour").document("data").collection("recommend")
                .document(cityName)
                .get()
                .await()

            if (document != null) {
                val contentIdList =
                    (document.data?.get("contentId") as? List<*>)?.filterIsInstance<String>()
                        ?: emptyList()

                resultList[CONTENT_ID_LIST] = contentIdList
                resultList[LIST_TYPE] = listType
            }

            UiState.Success(resultList)
        } catch (exception: Exception) {
            Log.d("TAG", "get failed ", exception)
            UiState.Error(exception)
        }
    }

    /**
     * 관광지 상세 조회 시 counting 기능
     */
    override suspend fun addCountingFromFireBase(
        contentId: String
    ) {
        val document = fireStore.collection("tour")
            .document("data")
            .collection("popular")
            .document(contentId)

        fireStore.runTransaction { transaction ->
            val snapshot = transaction.get(document)
            val currentCount = snapshot.getLong("counting") ?: 0
            transaction.set(document, mapOf("counting" to currentCount + 1))
        }.addOnSuccessListener {
            Log.d("TAG", "Transaction success!")
        }.addOnFailureListener { e ->
            Log.w("TAG", "Transaction failure.", e)
        }
    }

    /**
     * user 좋아요 리스트 조회
     */
    override suspend fun getUserLikeFromFireBase(
        userId: String
    ): UiState<MutableList<String>> {
        return try {
            val result = mutableListOf<String>()
            val snapshot = fireStore.collection("user")
                .document(userId)
                .collection("like")
                .get()
                .await()

            for (document in snapshot.documents) {
                result.add(document.id)
            }

            UiState.Success(result)
        } catch (exception: Exception) {
            Log.d("TAG", "get failed ", exception)
            UiState.Error(exception)
        }
    }

    /**
     * 관광지 좋아요 상태 업데이트
     *
     * 1. document에 contentId가 있으면 삭제하여 좋아요 취소
     * 2. document에 contentId가 없으면 추가하여 좋아요 추가
     */
    override suspend fun updateUserLikeFromFireBase(
        userId: String,
        contentId: String
    ) {
        val fireStore = fireStore.collection("user")
            .document(userId)
            .collection("like")

        val document = fireStore.document(contentId).get().await()

        if (document.exists()) {
            fireStore.document(contentId).delete().await()
        } else {
            fireStore.document(contentId).set(mapOf("testData" to true)).await()
        }
    }
}