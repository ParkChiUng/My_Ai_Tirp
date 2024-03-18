package com.sessac.myaitrip.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sessac.myaitrip.data.entities.TourItem

@Dao
interface TourDao {

    @Query("SELECT * FROM tbl_tour WHERE title IN (:title) AND firstImage != ''")
    fun getTourListByTitle(title: List<String>): List<TourItem>

    @Query("SELECT contentId FROM tbl_tour WHERE title LIKE (:title) AND firstImage != ''")
    fun getContentIdByTitle(title: String): List<String>

    @Query("SELECT * FROM tbl_tour WHERE contentId IN (:contentId) AND firstImage != ''")
    fun getTourList(contentId: List<String>): List<TourItem>

    @Query("SELECT * FROM tbl_tour WHERE address IN (:area) AND category1 = :category AND firstImage != ''")
    fun getTourList(area: String, category: String): List<TourItem>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTour(tourItem: List<TourItem>)

}