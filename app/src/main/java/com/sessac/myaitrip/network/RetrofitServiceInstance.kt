package com.sessac.myaitrip.network

import com.sessac.myaitrip.data.api.TourApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitServiceInstance {
    companion object {
        private lateinit var tourService: TourApiService

        private const val tourApiUrl = "http://apis.data.go.kr/B551011/KorService1/"

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
    }
}