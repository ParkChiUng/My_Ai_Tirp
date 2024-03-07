package com.sessac.myaitrip.data.tour

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
@Dao
interface TourDao {

    @Query("SELECT * FROM tbl_tour WHERE title IN (:title) AND firstImage != ''")
    fun getTourListByTitle(title: List<String>): Flow<List<TourItem>>

//    @Query("SELECT contentId FROM tbl_tour WHERE title LIKE (:title) AND firstImage != '' AND (areaCode == 35 OR areaCode == 36)")
//    @Query("SELECT contentId FROM tbl_tour WHERE title LIKE (:title) AND firstImage != '' AND areaCode  == 39")
    @Query("SELECT contentId FROM tbl_tour WHERE title LIKE (:title) AND firstImage != ''")
    fun getContentIdByTitle(title: String): Flow<List<String>>

    @Query("SELECT * FROM tbl_tour WHERE contentId IN (:contentId) AND firstImage != ''")
    fun getTourListById(contentId: List<String>): Flow<List<TourItem>>

    @Transaction
    @Insert
    fun insertTour(tourItem: List<TourItem>)

}