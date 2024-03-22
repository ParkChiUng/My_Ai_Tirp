package com.sessac.myaitrip.presentation.tourDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.geometry.LatLng
import com.sessac.myaitrip.R
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentTourMapDetailBottomSheetBinding
import com.sessac.myaitrip.util.GlideUtil
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

            calculateDistance()?.let {
                Log.e("calculateDistance", "calculateDistance() = $it")
                tvTourDetailBottomSheetDistance.visibility = View.VISIBLE
                tvTourDetailBottomSheetDistance.text = resources.getString(R.string.distance_format, it) // 거리
            }

            // 관광지 이미지
            if(tourData.firstImage.isNotEmpty())
                GlideUtil.loadImage(ivTourDetailBottomSheetImg.context, tourData.firstImage, ivTourDetailBottomSheetImg)
            else if(tourData.firstImage2.isNotEmpty())
                GlideUtil.loadImage(ivTourDetailBottomSheetImg.context, tourData.firstImage2, ivTourDetailBottomSheetImg)

            // TODO. 좋아요 개수 or 조회수
//            tvTourDetailBottomSheetLikeCount
        }
    }

    fun calculateDistance(): Double? {
        myLocatePosition?.let { myLocation ->
            val earthRadius = 6371.01 // 지구 반지름 (킬로미터 단위)

            val dLat = Math.toRadians(abs(myLocation.latitude - tourData.mapY.toDouble()))
            val dLon = Math.toRadians(abs(myLocation.longitude - tourData.mapX.toDouble()))

            val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(myLocation.latitude)) * cos(Math.toRadians(tourData.mapY.toDouble())) * sin(dLon / 2).pow(2.0)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return earthRadius * c
        } ?: return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}