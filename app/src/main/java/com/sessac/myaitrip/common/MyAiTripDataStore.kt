package com.sessac.myaitrip.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile

private const val USER_PREF_KEY = "user_prefs"
class MyAiTripDataStore(private val context: Context) {

    val userDataStore = PreferenceDataStoreFactory.create(
        produceFile = {
            context.preferencesDataStoreFile(USER_PREF_KEY)
        }
    )
}