package com.sessac.myaitrip.presentation.tourmap

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.gun0912.tedpermission.coroutine.TedPermission
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.sessac.myaitrip.databinding.FragmentTourMapBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.util.showToast
import kotlinx.coroutines.launch

/**
 * 맵 페이지
 */
class TourMapFragment
    : ViewBindingBaseFragment<FragmentTourMapBinding>(FragmentTourMapBinding::inflate),
    OnMapReadyCallback {

    private val TAG = TourMapFragment::class.java.name

    private lateinit var mapView: MapView
    private lateinit var map: NaverMap

    private lateinit var fusedLocationSource: FusedLocationSource

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
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

            /**
             * 카메라가 움직일 때 이벤트 처리 리스너
             */
            naverMap.addOnCameraChangeListener { reason, animated ->
//                Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
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
    private fun updateMyLocation() {
        lifecycleScope.launch {
            val permissionResult = TedPermission.create()
                .setDeniedMessage("원할한 기능 사용을 위해 사진 및 위치 권한을 허용해주세요.")
                .setPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .check()

            if (permissionResult.isGranted) {
                fusedLocationSource = FusedLocationSource(requireActivity(), LOCATION_PERMISSION_REQUEST_CODE)

                with(map) {
                    locationSource = fusedLocationSource        // 현재 위치
                    locationTrackingMode = LocationTrackingMode.Follow // 위치를 추적하면서 카메라도 따라 움직인다.
                    binding.btnTourMapMyLocation.isSelected = true
                }
            } else {
                requireContext().showToast("현재 위치 기능을 사용하려면, 위치 권한을 허용해주세요.")
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