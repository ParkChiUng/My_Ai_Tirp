package com.sessac.myaitrip.common

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.common.KakaoSdk
import com.sessac.myaitrip.BuildConfig

class MyAiTripApplication: Application() {
    companion object {
        private lateinit var myAiTripApplication: MyAiTripApplication
        private lateinit var firebaseAuth: FirebaseAuth

        fun getInstance(): MyAiTripApplication = myAiTripApplication
        fun getContext(): Context = getInstance().applicationContext

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
    }
}