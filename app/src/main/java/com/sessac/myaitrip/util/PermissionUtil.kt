package com.sessac.myaitrip.util

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission as CoroutinePermission
import com.gun0912.tedpermission.normal.TedPermission as NormalPermission

object PermissionUtil {
    suspend fun requestPermissionResultByCoroutine(vararg permissons: String): TedPermissionResult {
        return CoroutinePermission.create()
            .setDeniedMessage("원할한 기능 사용을 위해 사진 및 위치 권한을 허용해주세요.")
            .setPermissions(*permissons)
            .check()
    }

    fun requestPermissionResultByNormal(vararg permissons: String, onPermissionGranted: () -> Unit, onPermissionDenied: () -> Unit ) {
        NormalPermission.create()
            .setPermissionListener( object: PermissionListener {
                override fun onPermissionGranted() {
                    onPermissionGranted()
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    onPermissionDenied()
                }
            })
            .setDeniedMessage("원할한 기능 사용을 위해 사진 및 위치 권한을 허용해주세요.")
            .setPermissions(*permissons)
            .check()
    }
}