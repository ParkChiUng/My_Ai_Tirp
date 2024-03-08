package com.sessac.myaitrip.presentation.progress

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.sessac.myaitrip.MainActivity
import com.sessac.myaitrip.databinding.ActivityProgressBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.service.TourDataUpdateService
import kotlinx.coroutines.launch

class ProgressActivity :
    ViewBindingBaseActivity<ActivityProgressBinding>({ ActivityProgressBinding.inflate(it) }) {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(this, TourDataUpdateService::class.java).also {
            startService(it)
        }

        progressBar = binding.progressBar

        lifecycleScope.launch {
            TourDataUpdateService.progressFlow.collect { (progress, max) ->
                when(max) {
                    -1 -> startMainActivity()
                    else -> {
                        updateProgress(progress, max)
                        if (progress == max) {
                            startMainActivity()
                        }
                    }
                }
            }
        }
    }

    private fun startMainActivity() {
        Intent(this@ProgressActivity, MainActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun updateProgress(progress: Int, max: Int) {
        val percentage = (progress.toFloat() / max.toFloat() * 100).toInt()
        with(progressBar) {
            this.max = max
            this.progress = progress
        }

        with(binding) {
            progressPercent.text = "$percentage%"
            progressCounting.text = "$progress / $max"
        }
    }
}