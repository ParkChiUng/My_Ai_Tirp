package com.sessac.myaitrip.util

import android.content.Context
import android.widget.Toast
import com.sessac.myaitrip.common.MyAiTripApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

suspend fun CoroutineScope.showToast(message: String) {
    withContext(Dispatchers.Main) {
        Toast.makeText(MyAiTripApplication.getContext(), message, Toast.LENGTH_SHORT).show()
    }
}