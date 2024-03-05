package com.sessac.myaitrip.data.tour.repository

import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.tour.TourItem
import kotlinx.coroutines.flow.Flow

class TourRepository private constructor() {

    private val tourDao = MyAiTripApplication.getRoomDatabase().tourDao()

    fun insertTour(newTourItem: List<TourItem>) {
        tourDao.insertTour(newTourItem)
    }

    fun getTourListByTitle(title: List<String>): Flow<List<TourItem>> {
        return tourDao.getTourListByTitle(title)
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
