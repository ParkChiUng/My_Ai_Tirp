package com.sessac.myaitrip.data.api

import com.sessac.myaitrip.BuildConfig
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.entities.remote.ApiResponse
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
}