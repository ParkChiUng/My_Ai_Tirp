package com.sessac.myaitrip.presentation.progress

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.ProgressBar
import com.sessac.myaitrip.MainActivity
import com.sessac.myaitrip.databinding.ActivityProgressBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.service.TourDataUpdateService

class ProgressActivity :
    ViewBindingBaseActivity<ActivityProgressBinding>({ ActivityProgressBinding.inflate(it) }) {

    private lateinit var progressBar: ProgressBar

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val receiveMax = intent.getIntExtra("max", 0)
            val receiveProgress = intent.getIntExtra("progress", 0)
            val percentage = (receiveProgress.toFloat() / receiveMax.toFloat() * 100).toInt()

            with(progressBar) {
                max = receiveMax
                progress = receiveProgress
            }

            with(binding) {
                progressPercent.text = "$percentage%"
                progressCounting.text = "$receiveProgress / $receiveMax"
            }

            if (receiveProgress == receiveMax) {
                Intent(context, MainActivity::class.java).also {
                    it.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, TourDataUpdateService::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startService(it)
        }

        progressBar = binding.progressBar

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Tiramisu 이상에서만 실행되는 코드
            registerReceiver(receiver, IntentFilter("tour_progress"), RECEIVER_NOT_EXPORTED)
        } else {
            // Tiramisu 미만에서 실행되는 코드
            registerReceiver(receiver, IntentFilter("tour_progress"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}