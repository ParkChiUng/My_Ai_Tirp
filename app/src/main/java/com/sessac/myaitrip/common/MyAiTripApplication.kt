package com.sessac.myaitrip.common

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.sessac.myaitrip.BuildConfig
import com.sessac.myaitrip.data.AppDatabase

class MyAiTripApplication : Application() {
    companion object {
        private lateinit var myAiTripApplication: MyAiTripApplication
        private lateinit var database: AppDatabase
        lateinit var appName: String
        fun getInstance(): MyAiTripApplication = myAiTripApplication
        fun getContext(): Context = getInstance().applicationContext
        fun getRoomDatabase() = database

    }

    override fun onCreate() {
        super.onCreate()
        myAiTripApplication = this
//        Log.e("keyHash", "keyHash = ${Utility.getKeyHash(this)}")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
        appName = applicationInfo.loadLabel(packageManager).toString()
        database = AppDatabase.getDatabase(this)
    }
}