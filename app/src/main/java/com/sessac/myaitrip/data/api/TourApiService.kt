package com.sessac.myaitrip.data.api

import com.sessac.myaitrip.BuildConfig
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.entities.remote.ApiResponse
import com.sessac.myaitrip.data.entities.remote.TourDetailItems
import com.sessac.myaitrip.data.entities.remote.TourImageItems
import com.sessac.myaitrip.data.entities.remote.LocationBasedTourItems
import com.sessac.myaitrip.data.entities.remote.TourItems
import retrofit2.http.GET
import retrofit2.http.Query

interface TourApiService {
    @GET("areaBasedList1")
    suspend fun getAllData(
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("_type") type: String = "json",
        @Query("MobileApp") mobileApp: String = MyAiTripApplication.appName,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("serviceKey") serviceKey: String = BuildConfig.TOUR_API_SERVICE_KEY,
    ): ApiResponse<TourItems>

    @GET("locationBasedList1")
    suspend fun getLocationBasedData(
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 1000,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("MobileApp") MobileApp: String = MyAiTripApplication.appName,
        @Query("_type") type: String = "json",
        @Query("mapX") longitude: String,
        @Query("mapY") latitude: String,
        @Query("radius") radius: String = "3000",
        @Query("serviceKey") serviceKey: String = BuildConfig.TOUR_API_SERVICE_KEY,
    ): ApiResponse<LocationBasedTourItems>

    @GET("detailCommon1")
    suspend fun getDetailData(
        @Query("contentId") contentId: String,
        @Query("overviewYN") overviewYN: String = "Y",
        @Query("_type") type: String = "json",
        @Query("MobileApp") mobileApp: String = MyAiTripApplication.appName,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("serviceKey") serviceKey: String = BuildConfig.TOUR_API_SERVICE_KEY,
    ): ApiResponse<TourDetailItems>

    @GET("detailImage1")
    suspend fun getImageData(
        @Query("contentId") contentId: String,
        @Query("subImageYN") subImageYN: String = "Y",
        @Query("_type") type: String = "json",
        @Query("MobileApp") mobileApp: String = MyAiTripApplication.appName,
        @Query("MobileOS") mobileOS: String = "AND",
        @Query("serviceKey") serviceKey: String = BuildConfig.TOUR_API_SERVICE_KEY,
    ): ApiResponse<TourImageItems>
}