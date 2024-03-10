package com.sessac.myaitrip.common

import android.app.Application
import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.kakao.sdk.common.KakaoSdk
import com.sessac.myaitrip.BuildConfig
import com.sessac.myaitrip.data.api.TourApiService
import com.sessac.myaitrip.data.database.AppDatabase
import com.sessac.myaitrip.network.RetrofitServiceInstance
import retrofit2.Retrofit

class MyAiTripApplication: Application() {
    private lateinit var dataStore: MyAiTripDataStore

    companion object {
        private lateinit var myAiTripApplication: MyAiTripApplication
        private lateinit var database: AppDatabase
        private lateinit var generativeModel: GenerativeModel
        lateinit var appName: String

        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var fireStore: FirebaseFirestore
        private lateinit var fireStorage: FirebaseStorage

        private lateinit var tourService: TourApiService

        fun getInstance(): MyAiTripApplication = myAiTripApplication
        fun getContext(): Context = getInstance().applicationContext
        fun getRoomDatabase() = database
        fun getGeminiModel() = generativeModel

        fun getTourApiService() = tourService
    }

    override fun onCreate() {
        super.onCreate()
        myAiTripApplication = this
        firebaseAuth = Firebase.auth
        fireStore = Firebase.firestore
        fireStorage = Firebase.storage
//        Log.e("keyHash", "keyHash = ${Utility.getKeyHash(this)}")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        appName = applicationInfo.loadLabel(packageManager).toString()
        database = AppDatabase.getDatabase(this)
        tourService = RetrofitServiceInstance.getTourApiService()

        generativeModel = GenerativeModel(
            // For text-only input, use the gemini-pro model
            modelName = "gemini-pro",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = BuildConfig.GEMINI_API_KEY
        )
        dataStore = MyAiTripDataStore(this)
    }

    fun getDataStore(): MyAiTripDataStore = run {
        if(!::dataStore.isInitialized) dataStore = MyAiTripDataStore(this)

        dataStore
    }

    fun getFirebaseAuth(): FirebaseAuth = firebaseAuth
    fun getFireStore(): FirebaseFirestore = fireStore
    fun getFireStorage(): FirebaseStorage = fireStorage
}