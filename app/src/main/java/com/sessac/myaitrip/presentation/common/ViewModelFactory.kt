package com.sessac.myaitrip.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.repository.tour.TourRepository
import com.sessac.myaitrip.data.repository.user.UserRepository
import com.sessac.myaitrip.data.repository.user.local.UserLocalDataSource
import com.sessac.myaitrip.data.repository.user.remote.UserRemoteDataSource
import com.sessac.myaitrip.data.repository.weather.WeatherRepository
import com.sessac.myaitrip.network.RetrofitServiceInstance
import com.sessac.myaitrip.presentation.login.LoginViewModel
import com.sessac.myaitrip.presentation.register.RegisterViewModel
import com.sessac.myaitrip.presentation.tourmap.TourMapViewModel

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

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                val userLocalDataSource = UserLocalDataSource(dataStore.userDataStore)
                val userRemoteDataSource = UserRemoteDataSource()
                val userRepository = UserRepository(userLocalDataSource, userRemoteDataSource)

                RegisterViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(TourMapViewModel::class.java) -> {
                val weatherApiService = RetrofitServiceInstance.getWeatherApiService()
                val weatherRepository = WeatherRepository(weatherApiService)
                val tourRepository = TourRepository.getInstance()

                TourMapViewModel(weatherRepository, tourRepository) as T
            }

            else -> {
                throw IllegalArgumentException("Failed to create ViewModel : ${modelClass.name}")
            }
        }
    }
}