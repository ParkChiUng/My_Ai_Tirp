package com.sessac.myaitrip.presentation.tourDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.geometry.LatLng
import com.sessac.myaitrip.R
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentTourMapDetailBottomSheetBinding
import com.sessac.myaitrip.presentation.common.TourDetailBottomSheetViewModel
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.util.GlideUtil
import com.sessac.myaitrip.util.repeatOnCreated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class TourDetailByMapBottomSheetFragment(
    private val tourData: TourItem,
    private val myLocatePosition: LatLng? = null
): BottomSheetDialogFragment() {

    private val tourDetailBottomSheetViewModel: TourDetailBottomSheetViewModel by viewModels { ViewModelFactory() }

    private var _binding: FragmentTourMapDetailBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val contentTypeMap  = mapOf(
        "12" to "관광지",
        "14" to "문화시설",
        "15" to "축제•공연•행사",
        "25" to "여행코스",
        "28" to "레포츠",
        "32" to "숙박",
        "38" to "쇼핑",
        "39" to "음식점"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTourMapDetailBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewCountCollection()  // 조회수 Collector
        tourDetailBottomSheetViewModel.getTourViewCount(tourData.contentId) // 조회수 가져오기

        with(binding) {
            tvTourDetailBottomSheetTitle.text = tourData.title // 제목
            tvTourDetailBottomSheetAddress.text = tourData.address
            if(tourData.address2.isNotEmpty()) {
                with(tvTourDetailBottomSheetSubAddress) {
                    visibility = View.VISIBLE
                    text = tourData.address2
                }
            }
            tvTourDetailBottomSheetType.text = contentTypeMap[tourData.contentTypeId]   // 타입

            tvTourDetailBottomSheetDistance.visibility = View.GONE

            lifecycleScope.launch {
                calculateDistance()?.let {
                    Log.e("calculateDistance", "calculateDistance() = $it")
                    tvTourDetailBottomSheetDistance.visibility = View.VISIBLE
                    tvTourDetailBottomSheetDistance.text = resources.getString(R.string.distance_format, it) // 거리
                }
            }

            // 관광지 이미지
            if(tourData.firstImage.isNotEmpty())
                GlideUtil.loadImage(ivTourDetailBottomSheetImg.context, tourData.firstImage, ivTourDetailBottomSheetImg)
            else if(tourData.firstImage2.isNotEmpty())
                GlideUtil.loadImage(ivTourDetailBottomSheetImg.context, tourData.firstImage2, ivTourDetailBottomSheetImg)

        }
    }

    private fun setupViewCountCollection() {
        repeatOnCreated {
            tourDetailBottomSheetViewModel.viewCountStatus.collectLatest { state ->
                when(state) {
                    is UiState.Loading -> {}
                    is UiState.Success -> {
                        binding.tvTourDetailBottomSheetViewCount.text = state.data.toString()
                    }
                    is UiState.Error -> {}
                    else -> {}
                }
            }
        }
    }

    private suspend fun calculateDistance() = withContext(Dispatchers.IO) {
        myLocatePosition?.let { myLocation ->
            val earthRadius = 6371.01 // 지구 반지름 (킬로미터 단위)

            val dLat = Math.toRadians(abs(myLocation.latitude - tourData.mapY.toDouble()))
            val dLon = Math.toRadians(abs(myLocation.longitude - tourData.mapX.toDouble()))

            val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(myLocation.latitude)) * cos(
                Math.toRadians(tourData.mapY.toDouble())
            ) * sin(dLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return@withContext earthRadius * c
        } ?: return@withContext null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}