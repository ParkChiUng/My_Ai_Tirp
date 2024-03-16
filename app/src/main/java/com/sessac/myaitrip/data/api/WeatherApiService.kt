package com.sessac.myaitrip.data.api

import com.sessac.myaitrip.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // 오늘 05시부터 다음날 05시까지 날씨 정보 가져오기
    @GET("getVilageFcst")
    suspend fun getWeatherData(
        @Query("serviceKey") serviceKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("pageNo") pageNo: String = "1",
        @Query("numOfRows") numOfRows: String = "300",
        @Query("dataType") dataType: String = "JSON",
        @Query("base_date") baseDate: String,
        @Query("nx") nx: String,
        @Query("ny") ny: String,
    )
}