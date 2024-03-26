package com.sessac.myaitrip.data.repository.tour.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.sessac.myaitrip.common.CONTENT_TYPE_ID_AREA
import com.sessac.myaitrip.common.DEFAULT_PAGE_NUMBER
import com.sessac.myaitrip.common.DEFAULT_TOTAL_COUNT
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.database.TourDao
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.local.TourPreferencesData
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource.TourPreferenceKeys.KEY_PAGE_NUMBER
import com.sessac.myaitrip.data.repository.tour.local.TourLocalDataSource.TourPreferenceKeys.KEY_TOTAL_COUNT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class TourLocalDataSource(
    private val tourDataStore: DataStore<Preferences>,
    private val tourDao: TourDao
) : ITourLocalDataSource {
    private object TourPreferenceKeys {
        val KEY_TOTAL_COUNT = intPreferencesKey("TOTAL_COUNT")
        val KEY_PAGE_NUMBER = intPreferencesKey("PAGE_NUMBER")
    }

    override suspend fun getTourPreferences(): Flow<TourPreferencesData> = tourDataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("Error reading preferences.", exception.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            mapTourPreferences(preferences)
        }

    private fun mapTourPreferences(preferences: Preferences): TourPreferencesData {
        val totalCount = preferences[KEY_TOTAL_COUNT] ?: DEFAULT_TOTAL_COUNT
        val pageNumber = preferences[KEY_PAGE_NUMBER] ?: DEFAULT_PAGE_NUMBER
        return TourPreferencesData(totalCount, pageNumber)
    }

    override suspend fun updatePreferences(totalCount: Int, pagerNumber: Int) {
        tourDataStore.edit { preferences ->
            preferences[KEY_TOTAL_COUNT] = totalCount
            preferences[KEY_PAGE_NUMBER] = pagerNumber
        }
    }

    override suspend fun getAIRecommendMap(prompt: String): Map<String, List<TourItem>> {
        val response = MyAiTripApplication.getGeminiModel().generateContent(prompt)

        return withContext(Dispatchers.IO) {
            val recommendMap = hashMapOf<String, MutableList<TourItem>>()

            response.text?.let { responseText ->
                Log.e("Gemini", responseText)

                // key : 지역
                // value: 지역 별 추천 관광지 목록

                responseText.replace("**", "")  // 마크다운 형태 제거
                    .lines()
                    .filter { it.isNotBlank() }
                    .forEach {
                        Log.e("gemini", "response Line = $it")
                        val locationName = it.split(": ")[0]
                        Log.e("gemini", "locationName = $locationName")
                        val locationAreaCode = CONTENT_TYPE_ID_AREA[locationName]
                        Log.e("gemini","areaCode = $locationAreaCode")

                        val tourText = it.split(": ")[1]
                        val keywords = tourText.split(", ")
                        Log.e("gemini", "keywords = $keywords")

                        keywords.forEach { keyword ->
//                            Log.e("gemini", "keyword = $keyword")
                            locationAreaCode?.let { areaCode ->
                                tourDao.getRecommendTourList(keyword, areaCode)
                                    .filter { tourItem -> tourItem.areaCode == locationAreaCode && tourItem.title.contains(keyword) }   // 지역에 해당하는 관광지로만
                                    .filter { tourItem -> tourItem.firstImage.isNotBlank() || tourItem.firstImage2.isNotBlank() }   // 이미지가 있는 관광지만
                                    .also { tourList ->
                                        // keyword를 포함하는 관광지 리스트
//                                        Log.e("gemini", "KeywordItems = $it")
                                        if(tourList.isNotEmpty()) {
                                            if(recommendMap[locationName] == null) {
                                                recommendMap[locationName] = mutableListOf()
                                            }

                                            recommendMap[locationName]?.let {
                                                it.addAll(tourList)
                                            } ?: {
                                                recommendMap[locationName] = mutableListOf()
                                                recommendMap[locationName]!!.addAll(tourList)
                                            }
                                        }
                                    }
                            }
                        }
                    }
            }
            recommendMap
        }
    }
}