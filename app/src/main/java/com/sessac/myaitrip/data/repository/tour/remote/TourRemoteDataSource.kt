package com.sessac.myaitrip.data.repository.tour.remote

import android.util.Log
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.entities.remote.Body
import com.sessac.myaitrip.data.entities.remote.TourItems
import com.sessac.myaitrip.presentation.common.UiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await

class TourRemoteDataSource : ITourRemoteDataSource {
    private val fireStore = MyAiTripApplication.getInstance().getFireStore()

    override suspend fun getTourFromAPI(tourItems: Body<TourItems>): UiState<Body<TourItems>> {
        return UiState.Success(tourItems)
    }

    override suspend fun getPopularTourListFromFireBase(listType: String): UiState<Map<String, Any>> {
        return CoroutineScope(Dispatchers.IO).async {
            val resultList = mutableMapOf<String, Any>()
            try {
                val documents = fireStore.collection("tour").document("data").collection("popular")
                    .get()
                    .await()

                val contentIdList = documents.map { doc ->
                    Pair(doc.id, doc.getLong("counting") ?: 0)
                }.sortedByDescending { it.second }
                    .take(5)
                    .map { it.first }

                resultList["contentIdList"] = contentIdList
                resultList["listType"] = listType

                UiState.Success(resultList)
            } catch (exception: Exception) {
                Log.d("TAG", "get failed with ", exception)
                UiState.Error(exception)
            }
        }.await()
    }

    override suspend fun getAreaRecommendTourListFromFireBase(
        listType: String,
        cityName: String
    ): UiState<Map<String, Any>> {
        return CoroutineScope(Dispatchers.IO).async {
            val resultList = mutableMapOf<String, Any>()
            try {
                val document = fireStore.collection("tour").document("data").collection("recommend")
                    .document(cityName)
                    .get()
                    .await()

                if (document != null) {
                    val contentIdList =
                        (document.data?.get("contentId") as? List<*>)?.filterIsInstance<String>()
                            ?: emptyList()

                    resultList["contentIdList"] = contentIdList
                    resultList["listType"] = listType
                }

                UiState.Success(resultList)
            } catch (exception: Exception) {
                Log.d("TAG", "get failed with ", exception)
                UiState.Error(exception)
            }
        }.await()
    }

}