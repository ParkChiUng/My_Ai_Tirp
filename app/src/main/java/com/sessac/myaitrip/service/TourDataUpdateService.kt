package com.sessac.myaitrip.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import com.sessac.myaitrip.network.RetrofitServiceInstance
import com.sessac.myaitrip.data.api.TourApiService
import com.sessac.myaitrip.data.repository.tour.TourRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TourDataUpdateService : Service() {

    companion object {
        val progressFlow =
            MutableSharedFlow<Pair<Int, Int>>(extraBufferCapacity = 1)  // Pair(progress, max)
    }

    private val apiClient: TourApiService = RetrofitServiceInstance.getTourApiService()
    private lateinit var tourRepository: TourRepository

    private lateinit var sharedPref: SharedPreferences
    private lateinit var broadCastIntent: Intent
    private var prefPageNumber = 1
    private var prefTotalCount = -1
    private val numOfRows = 1000

    override fun onCreate() {
        super.onCreate()

        sharedPref = getSharedPreferences("tour_prefs", Context.MODE_PRIVATE)
        prefTotalCount = sharedPref.getInt("lastSaveTotalCount", -1)
        prefPageNumber = sharedPref.getInt("lastSavePageNumber", 1)
        broadCastIntent = Intent("tour_progress")
        tourRepository = TourRepository.getInstance()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /**
     * START_STICKY : 앱 강제 종료되었을 때 재시작 함.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            checkTourItemFromApi()
        }
        return START_STICKY
    }

    /**
     * 관광지 1개 아이템 호출
     *
     * sharedPref
     * 1. totalCount : 관광지 api 총 아이템 개수 저장. 아이템 개수가 다를 때 데이터 다시 조회 및 저장
     * 2. prefPageNumber : 조회된 pageNumber를 저장하여, 만약 강제 종료 후 재실행 시 마지막 저장한 곳 부터 저장함.
     *
     */
    private suspend fun checkTourItemFromApi() {
        try {
            val response = apiClient.getAllData(1, 1)
            val apiResultCode = response.response?.header?.resultCode

            if (apiResultCode == "0000") {
                val totalCount: Int = response.response?.body?.totalCount ?: 0
                val lastPageNumber = (totalCount + (numOfRows - 1)) / numOfRows

                if (prefTotalCount == -1 || totalCount > prefTotalCount || lastPageNumber != prefPageNumber) {
                    CoroutineScope(Dispatchers.IO).launch {
                        getTourItemFromApi()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        progressFlow.tryEmit(Pair(prefPageNumber, lastPageNumber))
                    }
                }
            }
        }catch (e: Exception){
            Log.d("test", "api error : $e")

            withContext(Dispatchers.Main) {
                progressFlow.tryEmit(Pair(-1, -1))
            }
        }
    }

    /**
     * 관광지 전체 리스트 호출
     *
     * 처음 설치 시, 아이템이 늘어난 경우, 마지막으로 저장한 pageNumber가 마지막이 아닌 경우 전체 리스트 조회 및 저장
     *
     */
    private suspend fun getTourItemFromApi() {
        while (true) {
            val response = apiClient.getAllData(prefPageNumber, numOfRows)
            val body = response.response?.body ?: break
            val apiResultCode = response.response?.header?.resultCode

            if (apiResultCode == "0000") {
                val totalCount = body.totalCount
                val lastPageNumber = (totalCount + (numOfRows - 1)) / numOfRows
                val tourItems = body.items?.item

                if (tourItems.isNullOrEmpty()) break

                with(sharedPref.edit()) {
                    putInt("lastSaveTotalCount", totalCount)
                    putInt("lastSavePageNumber", prefPageNumber)
                    apply()
                }

                withContext(Dispatchers.Main) {
                    progressFlow.tryEmit(Pair(prefPageNumber, lastPageNumber))
                }

                tourRepository.insertTour(tourItems)

                if (++prefPageNumber > lastPageNumber) break

            } else {
                break
            }
        }
    }
}
