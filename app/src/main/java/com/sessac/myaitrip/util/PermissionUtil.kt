package com.sessac.myaitrip.util

import com.gun0912.tedpermission.TedPermissionResult
import com.gun0912.tedpermission.coroutine.TedPermission

object PermissionUtil {
    suspend fun requestPermissionResult(vararg permissons: String): TedPermissionResult {
        return TedPermission.create()
            .setDeniedMessage("원할한 기능 사용을 위해 사진 및 위치 권한을 허용해주세요.")
            .setPermissions(*permissons)
            .check()
    }
}