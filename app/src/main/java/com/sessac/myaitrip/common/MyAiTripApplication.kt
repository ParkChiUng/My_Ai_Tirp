package com.sessac.myaitrip.common

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import com.sessac.myaitrip.BuildConfig

class MyAiTripApplication: Application() {
    companion object {
        private lateinit var myAiTripApplication: MyAiTripApplication

        fun getInstance(): MyAiTripApplication = myAiTripApplication
        fun getContext(): Context = getInstance().applicationContext
    }
    override fun onCreate() {
        super.onCreate()
        myAiTripApplication = this
//        Log.e("keyHash", "keyHash = ${Utility.getKeyHash(this)}")
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_KEY)
    }
}