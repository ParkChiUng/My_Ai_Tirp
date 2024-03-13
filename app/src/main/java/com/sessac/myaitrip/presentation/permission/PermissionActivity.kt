package com.sessac.myaitrip.presentation.permission

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.login.LoginActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PermissionActivity : AppCompatActivity() {

    private val permissionViewModel: PermissionViewModel by viewModels{ ViewModelFactory(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupCheckPermissionStatusCollection()

        // 권한 체크가 끝나고 로그인 화면으로 이동한다.
        // 자동 로그인은 로그인 화면에서 하자.
    }

    private fun moveToLogin() {
        Intent(this, LoginActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun setupCheckPermissionStatusCollection() {
        lifecycleScope.launch {
            permissionViewModel.checkPermissionStatus.collectLatest {
                when (it) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        it.data.deniedPermissions?.let { deniedPermissionList ->
                            deniedPermissionList.forEach { deniedPermission ->
                                when (deniedPermission) {
                                    Manifest.permission.READ_EXTERNAL_STORAGE -> {

                                    }

                                    Manifest.permission.READ_MEDIA_IMAGES -> {

                                    }

                                    Manifest.permission.ACCESS_FINE_LOCATION -> {

                                    }

                                    Manifest.permission.ACCESS_COARSE_LOCATION -> {

                                    }

                                    else -> {}
                                }
                            }

                        }

                        moveToLogin()
                    }

                    is UiState.Error -> {
                        // TODO. Error Handling
                        moveToLogin()
                    }

                    else -> {}
                }
            }
        }
    }


}