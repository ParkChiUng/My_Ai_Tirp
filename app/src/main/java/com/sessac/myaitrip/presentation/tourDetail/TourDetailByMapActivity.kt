package com.sessac.myaitrip.presentation.tourDetail

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.ActivityTourDetailByMapBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.presentation.tourmap.TourMapFragment
import com.sessac.myaitrip.util.PermissionUtil
import com.sessac.myaitrip.util.showToast

class TourDetailByMapActivity : ViewBindingBaseActivity<ActivityTourDetailByMapBinding>(ActivityTourDetailByMapBinding::inflate), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap

    private var tourItem: TourItem? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val LOCATION_REQUEST_INTERVAL = 5 * 60 * 1000 // 5분마다 내 위치와 관광지 위치의 거리 구하기
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationSource: FusedLocationSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            mapView = binding.mapViewTourDetailByMap.also {
                it.onCreate(savedInstanceState)
                it.getMapAsync(this@TourDetailByMapActivity)
            }

            // 뒤로가기 버튼
            tbTourDetailByMap.setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun tourItemOnMap() {
        intent?.let { intent ->
            tourItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("tourItem", TourItem::class.java)
            } else {
                intent.getParcelableExtra("tourItem")
            }

            tourItem?.let { tourItem ->
                Log.e("tourDetailByMap", tourItem.toString())
                val tourPosition = LatLng(tourItem.mapY.toDouble(), tourItem.mapX.toDouble())
                naverMap.moveCamera(CameraUpdate.scrollTo(tourPosition))

                Marker().apply {
                    icon = MarkerIcons.BLUE
                    position = tourPosition
                    map = naverMap
                    width = 60
                    height = 86

                    anchor = Marker.DEFAULT_ANCHOR
                    captionText = tourItem.title
                    setCaptionAligns(Align.Bottom)
                    captionColor = Color.BLACK
                    captionHaloColor = Color.WHITE

                    onClickListener = Overlay.OnClickListener {
                        getMyLocatePosition()
                        true
                    }
                }
            }
        }
    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        with(naverMap) {
            uiSettings.isZoomControlEnabled = false
        }
        tourItemOnMap() // 관광지 정보 및 마커
        getMyLocatePosition() // 거리를 구하기 위한 위치 권한
    }

    @SuppressLint("MissingPermission")
    private fun getMyLocatePosition() {
        PermissionUtil.requestPermissionResultByNormal(
            *LOCATION_PERMISSIONS,
            onPermissionGranted = {

                fusedLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

                with(naverMap) {
                    locationSource = fusedLocationSource        // 현재 위치
                    locationTrackingMode = LocationTrackingMode.NoFollow // 위치를 추적하면서 카메라도 따라 움직인다.
                }

                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

                // 5분마다 내 위치와 관광지 위치의 거리 구하기
                val locationRequest = LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_REQUEST_INTERVAL.toLong())
                    .setWaitForAccurateLocation(false)
                    .build()

                // 현재 위치 콜백
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        locationResult.locations.forEachIndexed { index, location ->

                            Log.e(
                                "tourDetailByMap",
                                "현재 위도 = ${location.latitude}, 경도 = ${location.longitude}"
                            )

                            val myLocatePosition = LatLng(location.latitude, location.longitude)  // 내 현재 위치

                            tourItem?.let {
                                val tourDetailBottomSheet = TourDetailByMapBottomSheetFragment(it, myLocatePosition)
                                if(isActivityAvailable()) {
                                    tourDetailBottomSheet.show(supportFragmentManager, tourDetailBottomSheet.tag)
                                }
                            }

                            // 현재 날짜 (yyyyMMdd) 형식 필요
    //                            Log.e(TAG, "현재 날짜(yyyyMMdd) = ${DateUtil.getCurrentDate()}")
    //                            Log.e(TAG, "현재 시간 = ${DateUtil.getCurrentHour()}")

    //                            val (nx, ny) = latLonToGrid(location.latitude, location.longitude)
    //                            Log.e(TAG,"X 좌표 = $nx, Y 좌표 = $ny")
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            },
            onPermissionDenied = {
                showToast("장소와 나와의 거리를 확인하기 위해서는 위치 권한이 필요합니다.")

                tourItem?.let {
                    val tourDetailBottomSheet = TourDetailByMapBottomSheetFragment(it, myLocatePosition = null)
                    if(isActivityAvailable()) {
                        tourDetailBottomSheet.show(supportFragmentManager, tourDetailBottomSheet.tag)
                    }
                }
            }
        )
    }

    private fun isActivityAvailable(): Boolean {
        return !this@TourDetailByMapActivity.isFinishing && !this@TourDetailByMapActivity.isDestroyed
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}