package com.sessac.myaitrip.data.api

import com.sessac.myaitrip.BuildConfig
import com.sessac.myaitrip.data.entities.remote.WeatherApiResponse
import com.sessac.myaitrip.data.entities.remote.WeatherItems
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // 시간 당, 평균적으로 12개의 WeatherInfo를 가져온다.
    // 24시간 분량만 가져오기
    @GET("getVilageFcst") // 단기예보
    suspend fun getWeatherData(
        @Query("serviceKey") serviceKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("pageNo") pageNo: String = "1",
        @Query("numOfRows") numOfRows: String = "${(12 * 24) + 1}",
        @Query("dataType") dataType: String = "JSON",
        @Query("base_date") baseDate: String,
        @Query("base_time") baseTime: String = "0500",
        @Query("nx") nx: String,
        @Query("ny") ny: String,
    ): WeatherApiResponse<WeatherItems>
}