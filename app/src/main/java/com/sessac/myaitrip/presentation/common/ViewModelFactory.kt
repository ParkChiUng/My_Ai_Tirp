package com.sessac.myaitrip.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.repository.user.local.UserLocalDataSource
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.data.repository.user.remote.UserRemoteDataSource
import com.sessac.myaitrip.presentation.login.LoginViewModel
import com.sessac.myaitrip.presentation.splash.SplashViewModel

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore = MyAiTripApplication.getInstance().getDataStore()

        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                val userLocalDataSource = UserLocalDataSource(dataStore.userDataStore)
                val userRemoteDataSource = UserRemoteDataSource()
                val userRepository = UserRepository(userLocalDataSource, userRemoteDataSource)

                LoginViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                val userLocalDataSource = UserLocalDataSource(dataStore.userDataStore)
                val userRemoteDataSource = UserRemoteDataSource()
                val userRepository = UserRepository(userLocalDataSource, userRemoteDataSource)

                SplashViewModel(userRepository) as T
            }

            else -> {
                throw IllegalArgumentException("Failed to create ViewModel : ${modelClass.name}")
            }
        }
    }
}