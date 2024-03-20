package com.sessac.myaitrip.network

import com.sessac.myaitrip.data.api.TourApiService
import com.sessac.myaitrip.data.api.WeatherApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitServiceInstance {
    companion object {
        private lateinit var tourService: TourApiService

        // 추후 ai api service로 변경 예정
        private lateinit var aiService: TourApiService

        private lateinit var weatherService: WeatherApiService

        private const val tourApiUrl = "http://apis.data.go.kr/B551011/KorService1/"
        private const val weatherApiBaseUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/"

        private fun getRetrofitInstance(url: String): Retrofit {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun getTourApiService(): TourApiService {
            if (!this::tourService.isInitialized) {
                tourService = getRetrofitInstance(tourApiUrl).create(TourApiService::class.java)
            }
            return tourService
        }

        fun getAIApiService(): TourApiService {
            if (!this::aiService.isInitialized) {
                aiService = getRetrofitInstance(tourApiUrl).create(TourApiService::class.java)
            }
            return aiService
        }

        fun getWeatherApiService(): WeatherApiService {
            if (!this::weatherService.isInitialized) {
                weatherService = getRetrofitInstance(weatherApiBaseUrl).create(WeatherApiService::class.java)
            }
            return weatherService
        }
    }
}