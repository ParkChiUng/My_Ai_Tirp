package com.sessac.myaitrip.presentation.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sessac.myaitrip.GlideApp
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.CONTENT_ID_LIST
import com.sessac.myaitrip.common.LIST_TYPE
import com.sessac.myaitrip.common.TOUR_CONTENT_ID
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.data.entities.remote.LocationBasedTourItem
import com.sessac.myaitrip.databinding.FragmentHomeBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.home.adapter.FullCardAdapter
import com.sessac.myaitrip.presentation.home.adapter.SmallCardAdapter
import com.sessac.myaitrip.util.PermissionUtil
import com.sessac.myaitrip.util.repeatOnCreated
import com.sessac.myaitrip.util.showToast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 홈
 *
 * 전체적 흐름
 * 1. userId 조회 ( Preference )
 * 2. 좋아요 목록 조회 ( FireBase )
 * 3. 좋아요 목록으로 Adapter 생성
 * 4. 관광지 리스트 조회 ( FireBase )
 * 5. 각 관광지 리스트 submit
 *
 */
class HomeFragment :
    ViewBindingBaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var recommendRecyclerView: RecyclerView
    private lateinit var nearbyRecyclerView: RecyclerView

    private lateinit var popularAdapter: FullCardAdapter
    private lateinit var recommendAdapter: SmallCardAdapter
    private lateinit var nearbyAdapter: SmallCardAdapter
    private lateinit var contentIdList: List<String>
    private lateinit var areaList: Array<String>
    private lateinit var cityName: String
    private lateinit var listType: HomeViewModel.ListType
    private lateinit var userId: String
    private lateinit var tourLikeList: MutableList<String>

    private val homeViewModel: HomeViewModel by viewModels() { ViewModelFactory() }

    private lateinit var mContext: Context

    // 위치
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    companion object {
        private const val LOCATION_REQUEST_INTERVAL = 30 * 60 * 1000 // 30분
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        areaList = resources.getStringArray(R.array.areas_home)
        cityName = areaList[0]

        tourLikeList = mutableListOf()

        init()

        requestMyLocation()
    }

    private fun init() {
        getUserId()
        setupUserCollect()
        clickEventHandler()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    /**
     * Update my location
     * 위치 권한이 있다면, 맵 실행시 사용자 현 위치로 이동
     */
    @SuppressLint("MissingPermission")
    private fun requestMyLocation() {
        lifecycleScope.launch {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext)

            val grantedResult = PermissionUtil.requestPermissionResultByCoroutine(*LOCATION_PERMISSIONS)

            if( grantedResult.isGranted ) {
                val locationRequest = LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_REQUEST_INTERVAL.toLong())
                    .setWaitForAccurateLocation(false)
                    .build()

                locationCallback = object: LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        locationResult.locations.forEachIndexed { _, location ->

                            Log.e("Home 내 위치", "현재 위도 = ${location.latitude}, 경도 = ${location.longitude}")

                            with(homeViewModel) {
                                getRecommendAroundTourList(location.latitude.toString(), location.longitude.toString())
                            }
                        }
                    }
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            } else {
                mContext.showToast("내 주변 관광지를 가져오려면, 위치 권한을 허용해주세요.")
            }
        }
    }

    private fun clickEventHandler() {

        /**
         * 지역 별 추천 관광지 chip 클릭 리스너
         */
        with(binding) {
            chipGroup.setOnCheckedStateChangeListener { group, _ ->
                cityName = when (group.checkedChipId) {
                    chipSeoul.id -> areaList[0]
                    chipIncheon.id -> areaList[1]
                    chipGyeonggi.id -> areaList[2]
                    chipGangwon.id -> areaList[3]
                    chipChungcheong.id -> areaList[4]
                    chipJeolla.id -> areaList[5]
                    chipGyeongsang.id -> areaList[6]
                    chipJeju.id -> areaList[7]
                    else -> areaList[0]
                }
                getAreaRecommendTourList()
            }
        }
    }

    /**
     * 인기 관광지 조회
     */
    private fun getPopularTourList() {
        listType = HomeViewModel.ListType.POPULAR
        Log.d("test", "getPopularTourList")
        homeViewModel.getPopularTourListFromFireBase(listType.toString())
    }

    /**
     * 지역 별 추천 관광지 조회
     */
    private fun getAreaRecommendTourList() {
        listType = HomeViewModel.ListType.AREA_RECOMMEND
        Log.d("test", "getAreaRecommendTourList")
        homeViewModel.getAreaRecommendTourListFromFireBase(listType.toString(), cityName)
    }

    /**
     * 유저 Id 조회
     */
    private fun getUserId() {
        homeViewModel.getUserPreferences()
    }

    /**
     * 유저 좋아요 조회
     */
    private fun getUserLikeList() {
        homeViewModel.getUserLikeListFromFireBase(userId)
    }

    private fun setupUserCollect() {
        /**
         * 유저 정보 조회
         */
        repeatOnCreated {
            homeViewModel.userPreferenceStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        userId = state.data.userId
                        getUserLikeList()
                        setUpFireBaseCollect()
                    }

                    else -> {}
                }
            }
        }

        /**
         * fireBase에 추천 관광지 데이터 insert
         */
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                homeViewModel.tourList.collect { tourList ->
//                    for (item in tourList) {
//                        Firebase.firestore.collection("tour").document("data")
//                            .collection("recommend").document(cityName)
//                            .update("contentId", FieldValue.arrayUnion(item.contentId))
//                            .addOnSuccessListener {
//                                Log.d("TAG", "DocumentSnapshot successfully updated!")
//                            }
//                            .addOnFailureListener { e ->
//                                Log.w("TAG", "Error updating document", e)
//                            }
//                    }
//                }
//            }
//        }
    }

    private fun setUpFireBaseCollect() {

        /**
         * FireBase 관광지 리스트 조회
         *
         * 1. popular   : 인기 관광지 리스트
         * 2. recommend : 추천 관광지 리스트
         */
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.fireBaseResult.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        contentIdList = state.data[CONTENT_ID_LIST] as List<String>
                        listType = HomeViewModel.ListType.valueOf(state.data[LIST_TYPE] as String)

                        homeViewModel.getTourListByContentId(contentIdList, listType)
                    }

                    is UiState.Loading -> {

                    }

                    is UiState.Error -> {
                        Log.e("TourAPI HandleState", "${state.errorMessage}")
                    }

                    else -> {
                        Log.e("TourAPI HandleState", "$state")
                    }
                }
            }
        }

        /**
         * FireBase 유저 좋아요 관광지 리스트 조회
         */
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.userLikeResult.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        tourLikeList = state.data
                        setupRecyclerviewAdapter()
                    }

                    is UiState.Error -> {
                        homeViewModel.getUserLikeListFromFireBase(userId)
                    }

                    else -> {}
                }
            }
        }
    }


    /**
     * Room에서 조회한 관광지 리스트 adapter submit
     *
     * 1. popular   : 인기 관광지 리스트
     * 2. recommend : 추천 관광지 리스트
     */
    private fun setupTourListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.popularTourList.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        popularAdapter.setTourList(state.data)
                        popularRecyclerView.scrollToPosition(0)
                    }

                    is UiState.Error -> {
                        Log.e(
                            "TourAPI HandleState",
                            "popularTourList error : ${state.errorMessage}"
                        )
                    }

                    is UiState.Loading -> {
                        Log.e("TourAPI HandleState", "popularTourList loading : $$state")
                    }

                    else -> {
                        Log.e("TourAPI HandleState", "popularTourList else : $state")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.areaRecommendTourList.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        recommendAdapter.setTourList(state.data)
                        recommendRecyclerView.scrollToPosition(0)
                    }

                    is UiState.Error -> {
                        Log.e(
                            "TourAPI HandleState",
                            "areaRecommendTourList error : ${state.errorMessage}"
                        )
                    }

                    is UiState.Loading -> {
                        Log.e("TourAPI HandleState", "areaRecommendTourList loading : $$state")
                    }

                    else -> {
                        Log.e("TourAPI HandleState", "areaRecommendTourList else : $state")
                    }
                }
            }
        }

        // 내 주변 관광지
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.nearbyTourList.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val tourList = mutableListOf<TourItem>()
                        state.data?.let { locationBasedTourItems: List<LocationBasedTourItem> ->
                            locationBasedTourItems.forEach {
                                tourList.add(it.toTourItem())
                            }
                        }

                        binding.ivHomeNearbyRecommendLoading.visibility = View.GONE // 로딩 창 안보이게

                        nearbyAdapter.setTourList(tourList)
                        nearbyRecyclerView.scrollToPosition(0)
                    }

                    is UiState.Error -> {

                    }

                    is UiState.Loading -> {
                        with(binding) {
                            ivHomeNearbyRecommendLoading.visibility = View.VISIBLE // 로딩 창 보이게
                            GlideApp.with(ivHomeNearbyRecommendLoading.context)
                                .load(R.drawable.progress_animation)
                                .into(ivHomeNearbyRecommendLoading)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * 각 리사이클러 뷰 설정
     */
    private fun setupRecyclerviewAdapter() {
        popularRecyclerView = binding.rcvPopular
        recommendRecyclerView = binding.rcvLocationRecommend
        nearbyRecyclerView = binding.rcvNearbyRecommend

        /**
         * 관광지 클릭 시
         */
        val itemOnClick: (TourItem) -> Unit = { tourItem ->
            val bundle = Bundle().apply {
                putString(TOUR_CONTENT_ID, tourItem.contentId)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }

        /**
         * 관광지 좋아요 클릭 시
         */
        val likeOnClick: (AppCompatImageView, String) -> Unit = { likeImage, contentId ->
            likeImage.tag = if (likeImage.tag == "false") "true" else "false"
            likeImage.setImageResource(if (likeImage.tag == "true") R.drawable.ic_like_selected else R.drawable.ic_like_white)
            homeViewModel.updateUserLikeListFromFireBase(userId, contentId)
        }

        /**
         * 관광지 좋아요 상태
         */
        val liked: (AppCompatImageView) -> Unit = { likeImage ->
            likeImage.tag = "true"
            likeImage.setImageResource(R.drawable.ic_like_selected)
        }

        /**
         * 1. itemOnClick : 관광지 클릭 시
         * 2. likeOnClick : 좋아요 버튼 클릭 시
         * 3. liked       : 관광지가 좋아요 상태일 때
         * 4. tourLikeList : FireBase 좋아요 리스트
         */
        popularAdapter = FullCardAdapter(itemOnClick, likeOnClick, liked, tourLikeList,
            viewLifecycleOwner.lifecycleScope
        )
        recommendAdapter = SmallCardAdapter(itemOnClick, likeOnClick, liked, tourLikeList,
            viewLifecycleOwner.lifecycleScope
        )
        nearbyAdapter = SmallCardAdapter(itemOnClick, likeOnClick, liked, tourLikeList,
            viewLifecycleOwner.lifecycleScope
        )

        popularRecyclerView.adapter = popularAdapter
        recommendRecyclerView.adapter = recommendAdapter
        nearbyRecyclerView.adapter = nearbyAdapter

        getPopularTourList()
        getAreaRecommendTourList()
        setupTourListCollect()
    }

    /**
     * gemini api를 통해 가져온 관광지 정보로 fireBase의 지역 별 추천 관광지에 insert한다.
     */
//    private fun insertFireBase() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                homeViewModel.geminiApi(getString(R.string.recommend_popular, cityName))
//            }
//        }
//    }
}