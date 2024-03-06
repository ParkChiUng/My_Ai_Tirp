package com.sessac.myaitrip.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.login.LoginActivity
import com.sessac.myaitrip.presentation.progress.ProgressActivity
import com.sessac.myaitrip.util.repeatOnStarted

class SplashActivity : AppCompatActivity() {
    private val splashViewModel: SplashViewModel by viewModels() { ViewModelFactory(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Data Store 값 확인해서 Login or Main으로 이동
        splashViewModel.getUserPreferences()

        repeatOnStarted {
            splashViewModel.userPreferenceStatus.collect { state ->
                when(state) {
                    is UiState.Success -> {
                        val autoLogin = state.data.autoLogin
                        val userId = state.data.userId

                        Log.d("Splash", "autoLogin =$autoLogin")
                        Log.d("Splash","userId = $userId")

                        if( autoLogin && userId.isNotBlank() ) moveToMain()
                        else moveToLogin()
                    }
                    else -> {}
                }
            }
        }
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