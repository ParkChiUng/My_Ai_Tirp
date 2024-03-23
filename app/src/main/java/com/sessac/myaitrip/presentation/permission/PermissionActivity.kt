package com.sessac.myaitrip.presentation.permission

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.sessac.myaitrip.presentation.login.LoginActivity
import com.sessac.myaitrip.util.PermissionUtil
import kotlinx.coroutines.launch

class PermissionActivity : AppCompatActivity() {
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
            /*val permissionResult = */
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13부터는 이미지 권한 없이도 PhotoPicker를 사용가능
                PermissionUtil.requestPermissionResultByCoroutine(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } else {
                PermissionUtil.requestPermissionResultByCoroutine(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }

            moveToLogin()

            // TODO. 허용되지 않은 퍼미션에 대해 처리할 것이 있으면?
            /*permissionResult.deniedPermissions?.let { deniedList ->
                deniedList.forEach { deniedPermission ->
                    when(deniedPermission) {
                        Manifest.permission.READ_EXTERNAL_STORAGE -> {

                        }

                        Manifest.permission.ACCESS_FINE_LOCATION -> {

                        }

                        Manifest.permission.ACCESS_COARSE_LOCATION -> {

                        }
                    }
                }
            }*/
        }
    }
}