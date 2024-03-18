package com.sessac.myaitrip.presentation.tourmap

import android.Manifest
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.sessac.myaitrip.R
import com.sessac.myaitrip.databinding.FragmentTourMapBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.util.DateUtil
import com.sessac.myaitrip.util.PermissionUtil
import com.sessac.myaitrip.util.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

/**
 * 맵 페이지
 */
class TourMapFragment
    : ViewBindingBaseFragment<FragmentTourMapBinding>(FragmentTourMapBinding::inflate),
    OnMapReadyCallback {

    private val tourMapViewModel: TourMapViewModel by viewModels { ViewModelFactory(requireContext()) }

    private lateinit var mapView: MapView
    private lateinit var map: NaverMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var fusedLocationSource: FusedLocationSource

    companion object {
        private const val TAG = "NaverMap"
        private const val LOCATION_REQUEST_INTERVAL = 60 * 60 * 1000 // 1시간
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

//        private const val WEATHER_INFO_CATEGORY_WIND_SPEED_EW = "UUU" // 풍속(동서)
//        private const val WEATHER_INFO_CATEGORY_WIND_SPEED_NS = "VVV" // 풍속(남북)
//        private const val WEATHER_INFO_CATEGORY_WIND_DIRECTION = "VEC" // 풍향
//        private const val WEATHER_INFO_CATEGORY_WIND_SPEED = "WSD" // 풍속
//        private const val WEATHER_INFO_CATEGORY_WAV = "WAV" // 파고
//        private const val WEATHER_INFO_CATEGORY_RAINFALL_VOLUME = "PCP" // 1시간 강수량
//        private const val WEATHER_INFO_CATEGORY_SNOWFALL_VOLUME = "SNO" // 적설량(눈 내리는 양)

        private const val WEATHER_INFO_CATEGORY_TEMPERATURE = "TMP" // 기온
        private const val WEATHER_INFO_CATEGORY_HIGH_TEMPERATURE = "TMX" // 최고 기온
        private const val WEATHER_INFO_CATEGORY_LOW_TEMPERATURE = "TMN" // 최저 기온
        private val WEATHER_INFO_TEMPERATURE_CATEGORY = arrayOf(
            WEATHER_INFO_CATEGORY_TEMPERATURE,
            WEATHER_INFO_CATEGORY_HIGH_TEMPERATURE,
            WEATHER_INFO_CATEGORY_LOW_TEMPERATURE
        )
        private const val WEATHER_INFO_CATEGORY_SKY_CODE = "SKY" // 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
        private const val WEATHER_INFO_CATEGORY_RAINFALL_CODE = "PTY" // 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
        private const val WEATHER_INFO_CATEGORY_RAINFALL_PROBABILITY = "POP" // 강수확률
        private const val WEATHER_INFO_CATEGORY_HUMIDITY = "REH" // 습도
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapViewTourMap.also {
            it.onCreate(savedInstanceState)
            it.getMapAsync(this)
        }

        setupWeatherStatusCollection()
        setUpLocationPlaceStatusCollection()
    }

    private fun setUpLocationPlaceStatusCollection() {
        lifecycleScope.launch {
            tourMapViewModel.locationTourStatus.collectLatest { state ->
                when(state) {
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        val response = state.data.response
                        response?.let { response ->
                            val header = response.header
                            val body = response.body

                            header?.let { header ->
                                if( header.resultCode == "0000" && header.resultMsg == "OK" ) {

                                    body?.let { body ->
                                        body.items?.let { tourItems ->
                                            val tourList = tourItems.item
                                            tourList?.let { tourList ->
                                                if(tourList.isNotEmpty()) {
                                                    val sortedTourList = tourList.sortedBy { tourItem ->
                                                        tourItem.distance.toDouble() // 가까운 순으로 정렬
                                                    }.also {
                                                        // adapter submitList(it)
                                                    }.forEach {
//                                                        Log.d(TAG, "TourItem = $it") // 잘 가져와짐
                                                    }

                                                }
                                            }


                                        }
                                    }
                                }
                            }
                        }
                    }
                    is UiState.Error -> {}
                    else -> {}
                }
            }
        }
    }

    private fun initMyLocationBottomSheet() {
        binding.locationBottomSheet.visibility = View.VISIBLE
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.locationBottomSheet)

        bottomSheetBehavior.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isFitToContents = true
            saveFlags = BottomSheetBehavior.SAVE_ALL
        }
    }

    /**
     * Lat lon to grid
     * 기상청 API가 원하는 위도, 경도를 X, Y 좌표로 변환하는 함수
     * @param lat
     * @param lon
     * @return
     */
    fun latLonToGrid(lat: Double, lon: Double): Pair<Int, Int> {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43 // 기준점 X좌표(GRID)
        val YO = 136 // 기1준점 Y좌표(GRID)

        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI

        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        var ra = Math.tan(Math.PI * 0.25 + (lat) * DEGRAD * 0.5)
        ra = re * sf / Math.pow(ra, sn)
        var theta = lon * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        val x = (ra * Math.sin(theta) + XO + 0.5).toInt()
        val y = (ro - ra * Math.cos(theta) + YO + 0.5).toInt()

        return Pair(x, y)
    }

    private fun setupWeatherStatusCollection() {
        lifecycleScope.launch {
            tourMapViewModel.weatherStatus.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {

                    }

                    is UiState.Success -> {
                        val response = state.data.response
                        val weatherItems = response?.body?.item?.weatherInfoItems

                        weatherItems?.let { weatherInfoList ->
                            // 오늘 날씨 정보
                            val todayWeatherList = weatherInfoList.filter {weatherInfo ->
                                weatherInfo.forecastDate == DateUtil.getCurrentDate()
                            }

                            // 최저, 최고 기온 구하기
                            // TMX - 최고 기온, TMN - 최저 기온
                            var lowTemperature: Int? = null
                            var highTemperature: Int? = null

                            // 오늘 기온 정보
                            val todayTemperatureList = todayWeatherList.filter { weatherInfo ->
                                weatherInfo.dataCategory in WEATHER_INFO_TEMPERATURE_CATEGORY
                            }

                            todayTemperatureList.forEach { weatherInfo ->
                                Log.d(TAG, "온도 정보 ${weatherInfo.dataCategory} = ${weatherInfo.forecastValue}")

                                when(weatherInfo.dataCategory) {
                                    WEATHER_INFO_CATEGORY_LOW_TEMPERATURE -> {
                                        // 오늘 일자에 TMN 값이 있다면
                                        lowTemperature = weatherInfo.forecastValue.toDouble().roundToInt()
                                    }
                                    WEATHER_INFO_CATEGORY_HIGH_TEMPERATURE -> {
                                        // 오늘 일자에 TMX 값이 있다면
                                        highTemperature = weatherInfo.forecastValue.toDouble().roundToInt()
                                    }
                                }
                            }

                            with(binding.locationBottomSheetLayout) {
                                lowTemperature = lowTemperature ?: todayTemperatureList.minOf { weatherItems -> weatherItems.forecastValue.toDouble() }.roundToInt()
                                highTemperature = highTemperature ?: todayTemperatureList.maxOf{ weatherItems -> weatherItems.forecastValue.toDouble() }.roundToInt()

                                tvLocationBottomSheetLowTemperature.text = getString(R.string.temperature_low_format, lowTemperature)
                                tvLocationBottomSheetHighTemperature.text = getString(R.string.temperature_high_format, highTemperature)
                            }

                            var isRainOrSnow = true // 눈이나 비가 오는가?

                            // 현재 시간 날씨 정보
                            val currentTimeWeatherInfoList = todayWeatherList.filter { weatherInfo ->
                                weatherInfo.forecastTime == DateUtil.getCurrentHourWithFormatted() // 현재 시간에 맞는 관측 값 가져오기
                            }

                            currentTimeWeatherInfoList.forEach { weatherInfo ->
//                                Log.d(TAG, "날씨 정보 = $weatherInfo")
                                with(binding.locationBottomSheetLayout) {
                                    when(weatherInfo.dataCategory) {
                                        WEATHER_INFO_CATEGORY_TEMPERATURE -> {
                                            // 현재 시각 기온
                                            tvLocationBottomSheetCurrentTemperature.text = getString(R.string.temperature_normal_format, weatherInfo.forecastValue.toDouble().roundToInt())
                                        }

                                        WEATHER_INFO_CATEGORY_HUMIDITY -> {
                                            // 현재 시각 습도
                                            tvLocationBottomSheetHumidity.text = getString(R.string.humidity_format, weatherInfo.forecastValue.toInt())
                                        }

                                        WEATHER_INFO_CATEGORY_RAINFALL_PROBABILITY -> {
                                            // 현재 시각 강수확률
                                            tvLocationBottomSheetRainProbability.text = getString(R.string.rain_probability_format, weatherInfo.forecastValue.toInt())
                                        }

                                        WEATHER_INFO_CATEGORY_RAINFALL_CODE -> {
                                            // 현재 시각 강수상태
                                            // 강수형태(PTY) 코드 : 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
                                            Log.e(TAG, "RAIN_FALL_CODE = ${weatherInfo.forecastValue}")
                                            when(weatherInfo.forecastValue) {
                                                "0" -> {
                                                    isRainOrSnow = false // 눈이나 비가 오지 않으면 하늘 상태로
                                                }

                                                "1", "4" -> {
                                                    // 비 이미지로
                                                    ivLocationBottomSheetWeather.setImageResource(R.drawable.ic_weather_rainy)
                                                }

                                                "2", "3" -> {
                                                    // 눈 이미지로
                                                    ivLocationBottomSheetWeather.setImageResource(R.drawable.ic_weather_snowing)
                                                }

                                                else -> {}
                                            }
                                        }

                                        else -> {
                                            // 나머지 정보는 사용하지 않음
                                        }
                                    }
                                }
                            }

                            Log.e(TAG, "isRainOrSnow = $isRainOrSnow")

                            // 눈이나 비가 오지 않으면 하늘 상태로 이미지 적용
                            if( !isRainOrSnow ) {
                                // 현재 시각 하늘상태
                                currentTimeWeatherInfoList.filter {
                                    it.dataCategory == WEATHER_INFO_CATEGORY_SKY_CODE
                                }.forEach { weatherInfo ->
                                    Log.e(TAG, "SKY_CODE = ${weatherInfo.forecastValue}")
                                    with(binding.locationBottomSheetLayout) {
                                        when(weatherInfo.forecastValue) {
                                            // 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
                                            "1" -> {
                                                ivLocationBottomSheetWeather.setImageResource(R.drawable.ic_weather_sunny)
                                            }
                                            "3" -> {
                                                ivLocationBottomSheetWeather.setImageResource(R.drawable.ic_weather_partly_cloudy)
                                            }
                                            "4" -> {
                                                ivLocationBottomSheetWeather.setImageResource(R.drawable.ic_weather_cloudy)
                                            }
                                            else -> {}
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is UiState.Error -> {
                        Log.e(TAG, "WeatherData Error ${state.errorMessage}")
                    }

                    else -> {}
                }
            }
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
//                    Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
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
                addOnCameraIdleListener {}
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
            logoGravity = Gravity.TOP or Gravity.END
            setLogoMargin(0, 24, 24, 0) // 로고 마진
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

                        initMyLocationBottomSheet()

                        locationResult.locations.forEachIndexed { index, location ->
                            val cameraUpdate = CameraUpdate.scrollTo(LatLng(location.latitude, location.longitude))
                                .animate(CameraAnimation.Easing, 2000)
                                .reason(1000)

                            map.moveCamera(cameraUpdate) // 현재 위치로 카메라 이동

                            Log.e(TAG, "현재 위도 = ${location.latitude}, 경도 = ${location.longitude}")
                            // 현재 날짜 (yyyyMMdd) 형식 필요
//                            Log.e(TAG, "현재 날짜(yyyyMMdd) = ${DateUtil.getCurrentDate()}")
//                            Log.e(TAG, "현재 시간 = ${DateUtil.getCurrentHour()}")

                            val (nx, ny) = latLonToGrid(location.latitude, location.longitude)
                            Log.e(TAG,"X 좌표 = $nx, Y 좌표 = $ny")

                            getAddress(location.latitude, location.longitude)

                            with(tourMapViewModel) {
                                // 날씨 정보 가져오기
                                // 위도, 경도 정수 값 필요
                                // 05시 기상청 발표 값 가져오기
                                when(DateUtil.getCurrentHour()) {
                                    in 6..23 -> {
                                        // if 현재시간 in 06 ~ 23 (오늘 날씨 정보 가져오기)
                                        getWeatherData(
                                            DateUtil.getCurrentDate(),
                                            pointX = nx.toString(),
                                            pointY = ny.toString()
                                        )
                                    }

                                    else -> {
                                        // 현재 시간 in 00 ~ 05 (어제 날씨 정보 가져오기)
                                        getWeatherData(
                                            DateUtil.getYesterdayDate(),
                                            pointX = nx.toString(),
                                            pointY = ny.toString()
                                        )
                                    }
                                }
                                // 현재 위도, 경도로 근처 관광지 정보 가져오기
                                getPlaceListByLocation(location.latitude.toString(), location.longitude.toString())
                            }
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

                with(binding) {
                    btnTourMapMyLocation.isSelected = true
                }
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

                            // 바텀 시트 지역명 나타내주기
                            val locationNameBuilder = StringBuilder()
                            locationNameBuilder.append(adminArea)
                                .append(" ")

                            locality?.let {
                                locationNameBuilder.append(it)
                            } ?: locationNameBuilder.append(subLocality)

                            binding.locationBottomSheetLayout.tvLocationBottomSheetLocationName.text = locationNameBuilder.toString()
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