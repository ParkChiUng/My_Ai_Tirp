package com.sessac.myaitrip.presentation.tourmap

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.sessac.myaitrip.databinding.FragmentTourMapBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.util.PermissionUtil
import com.sessac.myaitrip.util.showToast
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 맵 페이지
 */
class TourMapFragment
    : ViewBindingBaseFragment<FragmentTourMapBinding>(FragmentTourMapBinding::inflate),
    OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: NaverMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationSource: FusedLocationSource

    companion object {
        private const val TAG = "NaverMap"
        private const val LOCATION_REQUEST_INTERVAL = 60 * 1000 // 60초
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapViewTourMap.also {
            it.onCreate(savedInstanceState)
            it.getMapAsync(this)
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        lifecycleScope.launch {
            map = naverMap

            initMapUi()
            updateMyLocation()

            /* 카메라 이동시키기
               val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881))
                .animate(CameraAnimation.Easing, 2000)
                .reason(1000)

                 naverMap.moveCamera(cameraUpdate)
*/
            with(map) {
                // 카메라가 움직일 때 이벤트 처리 리스너
                addOnCameraChangeListener { reason, animated ->
                    Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
                    /*
                        public static final int REASON_DEVELOPER = 0;   (개발자 API 호출)
                        public static final int REASON_GESTURE = -1;    (사용자 제스처)
                        public static final int REASON_CONTROL = -2;    (사용자의 버튼 선택)
                        public static final int REASON_LOCATION = -3;  (위치 정보 갱신)
                     */

                    // 사용자 제스처가 있을 때, 현재 위치 버튼이 선택되어 있다면 선택 해제
                    if(reason == -1)
                        binding.btnTourMapMyLocation.isSelected = false
                }

                // 카메라가 멈췄을 때, 리스너
                addOnCameraIdleListener {
                    getAddress(cameraPosition.target.latitude, cameraPosition.target.longitude)
                }
            }

            binding.btnTourMapMyLocation.setOnClickListener {
                updateMyLocation()
            }
        }
    }

    private fun initMapUi() {
        with(map.uiSettings) {
            isCompassEnabled = false    // 나침반
            isZoomControlEnabled = false    // +, - 줌 버튼
            setLogoMargin(24, 0, 0, 24) // 로고 마진
        }
    }

    /**
     * Update my location
     * 위치 권한이 있다면, 맵 실행시 사용자 현 위치로 이동
     */
    @SuppressLint("MissingPermission")
    private fun updateMyLocation() {
        PermissionUtil.requestPermissionResultByNormal(
            *LOCATION_PERMISSIONS,
            onPermissionGranted = {
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
                val locationRequest = LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_REQUEST_INTERVAL.toLong())
                    .setWaitForAccurateLocation(false)
                    .build()

                locationCallback = object: LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        locationResult.locations.forEachIndexed { index, location ->
                            val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                                .animate(CameraAnimation.Easing, 2000)
                                .reason(1000)

                            map.moveCamera(cameraUpdate)
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )

                fusedLocationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)

                with(map) {
                    locationSource = fusedLocationSource        // 현재 위치
                    locationTrackingMode = LocationTrackingMode.Follow // 위치를 추적하면서 카메라도 따라 움직인다.
                }

                binding.btnTourMapMyLocation.isSelected = true

            },
            onPermissionDenied = {
                requireContext().showToast("현재 위치 기능을 사용하려면, 위치 권한을 허용해주세요.")
            }
        )
    }

    // 좌표 -> 주소 변환
    private fun getAddress(latitude: Double, longitude: Double) {
        val geoCoder = Geocoder(requireContext(), Locale.KOREA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val geocodeListener = @RequiresApi(33) object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if(addresses.isNotEmpty()) {
                        with(addresses[0]) {
                            // Log.e(TAG, "countryName = $countryName") // 대한민국
                            Log.e(TAG, "adminArea = $adminArea") // 서울특별시
                            Log.e(TAG, "locality = $locality") // null
                            Log.e(TAG, "subLocality = $subLocality") // 관악구
                            // Log.e(TAG, "thoroughfare = $thoroughfare") 신림동
                        }
                    }
                }
                override fun onError(errorMessage: String?) {
                    requireContext().showToast("주소가 발견되지 않았습니다 $errorMessage")
                }
            }

            geoCoder.getFromLocation(latitude, longitude, 7, geocodeListener)

        } else { // API 레벨이 33 미만인 경우
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            addresses?.let{
                requireContext().showToast(addresses[0].getAddressLine(0))
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}