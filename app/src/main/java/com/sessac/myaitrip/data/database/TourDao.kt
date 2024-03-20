package com.sessac.myaitrip.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.sessac.myaitrip.data.entities.TourItem

@Dao
interface TourDao {

//    @Query("SELECT * FROM tbl_tour WHERE title IN (:title) AND firstImage != ''")
//    fun getTourListByTitle(title: List<String>): List<TourItem>

//    @Query("SELECT contentId FROM tbl_tour WHERE title LIKE (:title) AND firstImage != ''")
//    fun getContentIdByTitle(title: String): List<String>

    /**
     * [Room DB] contentId List로 관광지 리스트 조회
     */
    @Query("SELECT * FROM tbl_tour WHERE contentId IN (:contentId) AND firstImage != ''")
    fun getTourList(contentId: List<String>): List<TourItem>

    /**
     * [Room DB] 지역 명과 카테고리로 관광지 리스트 조회 ( 페이징 )
     */
    @Query("SELECT * FROM tbl_tour WHERE areaCode = :area AND contentTypeId = :category AND TRIM(firstImage, address) <> '' LIMIT :pageSize OFFSET :offset")
    fun getTourList(area: String, category: String, pageSize: Int, offset: Int): List<TourItem>

    /**
     * [Room DB] 지역 명, 카테고리, 검색어로 관광지 리스트 조회 ( 페이징 )
     */
    @Query("SELECT * FROM tbl_tour WHERE title LIKE '%' || :inputText || '%' AND areaCode = :area AND contentTypeId = :category AND TRIM(firstImage, address) <> '' LIMIT :pageSize OFFSET :offset")
    fun getTourList(area: String, category: String, inputText: String, pageSize: Int, offset: Int): List<TourItem>

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTour(tourItem: List<TourItem>)

}