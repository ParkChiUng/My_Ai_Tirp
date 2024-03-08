package com.sessac.myaitrip.data.tour.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.tour.TourItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

class TourRepository private constructor() {

    private val tourDao = MyAiTripApplication.getRoomDatabase().tourDao()

    fun insertTour(newTourItem: List<TourItem>) {
        tourDao.insertTour(newTourItem)
    }

    suspend fun getTourListByTitle(titles: List<String>): Flow<List<TourItem>> {
        val result = mutableListOf<String>()
        for (title in titles) {
            val tourList = tourDao.getContentIdByTitle(title).first()
            if(tourList.isNotEmpty())
                result.add(tourList.first())
        }
        return tourDao.getTourListById(result)
    }

    fun getTourListById(contentIdList: List<String>): Flow<List<TourItem>>{
        return tourDao.getTourListById(contentIdList)
    }

    companion object {
        private var instance: TourRepository? = null

        fun getInstance(): TourRepository {
            if (instance == null) {
                instance = TourRepository()
            }
            return instance!!
        }
    }
}
