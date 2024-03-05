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

    @Query("SELECT contentId FROM tbl_tour WHERE title LIKE (:title)")
    fun getTourListByTitle2(title: String): Flow<List<String>>

//    @RawQuery(observedEntities = [TourItem::class])
//    fun getTourListByTitle2(query: SupportSQLiteQuery): Flow<List<TourItem>>

    @Query("SELECT * FROM tbl_tour WHERE contentId IN (:contentId) AND firstImage != ''")
    fun getTourListById(contentId: List<String>): Flow<List<TourItem>>

    @Transaction
    @Insert
    fun insertTour(tourItem: List<TourItem>)

}