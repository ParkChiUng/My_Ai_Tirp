package com.sessac.myaitrip.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sessac.myaitrip.presentation.login.LoginActivity
import com.sessac.myaitrip.presentation.progress.ProgressActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // TODO. Data Store 값 확인해서 Login or Main으로 이동

        moveToLogin()
    }

    private fun moveToMain() {
        Intent(this, ProgressActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun moveToLogin() {
        Intent(this, LoginActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }
}