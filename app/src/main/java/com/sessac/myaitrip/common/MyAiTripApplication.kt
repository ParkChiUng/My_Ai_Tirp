package com.sessac.myaitrip.common

import android.app.Application
import android.content.Context
import androidx.datastore.dataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import com.sessac.myaitrip.BuildConfig
import com.sessac.myaitrip.data.AppDatabase

class MyAiTripApplication: Application() {
    private lateinit var dataStore: MyAiTripDataStore
    companion object {
        private lateinit var myAiTripApplication: MyAiTripApplication
        private lateinit var database: AppDatabase
        lateinit var appName: String
        private lateinit var firebaseAuth: FirebaseAuth


        fun getInstance(): MyAiTripApplication = myAiTripApplication
        fun getContext(): Context = getInstance().applicationContext
        fun getRoomDatabase() = database


        fun getFirebaseAuth(): FirebaseAuth = run {
            if(!::firebaseAuth.isInitialized) firebaseAuth = Firebase.auth

            firebaseAuth
        }
    }
    override fun onCreate() {
        super.onCreate()
        myAiTripApplication = this
        firebaseAuth = Firebase.auth
//        Log.e("keyHash", "keyHash = ${Utility.getKeyHash(this)}")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        appName = applicationInfo.loadLabel(packageManager).toString()
        database = AppDatabase.getDatabase(this)
        dataStore = MyAiTripDataStore(this)
    }

    fun getDataStore(): MyAiTripDataStore = run {
        if(!::dataStore.isInitialized) dataStore = MyAiTripDataStore(this)

        dataStore
    }
}