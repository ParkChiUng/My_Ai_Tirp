package com.sessac.myaitrip.presentation.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sessac.myaitrip.common.MyAiTripApplication
import com.sessac.myaitrip.data.repository.user.datastore.UserDataStoreRepository
import com.sessac.myaitrip.data.repository.user.firebase.FirebaseAuthRepository
import com.sessac.myaitrip.presentation.login.LoginViewModel

class ViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataStore = MyAiTripApplication.getInstance().getDataStore()

        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                val userDataStoreRepository = UserDataStoreRepository(dataStore.userDataStore)
                val firebaseAuthRepository = FirebaseAuthRepository()
                LoginViewModel(userDataStoreRepository, firebaseAuthRepository) as T
            }

            else -> {
                throw IllegalArgumentException("Failed to create ViewModel : ${modelClass.name}")
            }
        }
    }
}