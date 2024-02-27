package com.sessac.myaitrip.common

import android.app.Application
import android.content.Context

class MyAiTripApplication: Application() {
    companion object {
        private lateinit var myAiTripApplication: MyAiTripApplication

        fun getInstance(): MyAiTripApplication = myAiTripApplication
        fun getContext(): Context = getInstance().applicationContext
    }
    override fun onCreate() {
        super.onCreate()
        myAiTripApplication = this
    }
}