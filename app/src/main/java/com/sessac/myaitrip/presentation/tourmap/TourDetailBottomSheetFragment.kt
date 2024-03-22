package com.sessac.myaitrip.presentation.tourmap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sessac.myaitrip.R
import com.sessac.myaitrip.data.entities.TourClusterItemData
import com.sessac.myaitrip.data.entities.TourClusterItemKey
import com.sessac.myaitrip.databinding.FragmentTourMapDetailBottomSheetBinding
import com.sessac.myaitrip.util.GlideUtil
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class TourDetailBottomSheetFragment(
    private val tourKey: TourClusterItemKey,
    private val tourData: TourClusterItemData,
    val itemClick: () -> Unit
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
            if(tourData.subAddress.isNotEmpty()) {
                with(tvTourDetailBottomSheetSubAddress) {
                    visibility = View.VISIBLE
                    text = tourData.subAddress
                }
            }
            tvTourDetailBottomSheetDistance.text = resources.getString(R.string.distance_format, tourData.distance.toDouble() / 1000) // 거리
            tvTourDetailBottomSheetType.text = contentTypeMap[tourData.contentTypeId]   // 타입

            // 관광지 이미지
            if(tourData.imageUrl.isNotEmpty())
                GlideUtil.loadImage(ivTourDetailBottomSheetImg.context, tourData.imageUrl, ivTourDetailBottomSheetImg)
            else if(tourData.subImageUrl.isNotEmpty())
                GlideUtil.loadImage(ivTourDetailBottomSheetImg.context, tourData.subImageUrl, ivTourDetailBottomSheetImg)

            // TODO. 좋아요 개수
//            tvTourDetailBottomSheetLikeCount

            root.clicks().onEach {

            }.launchIn(lifecycleScope)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}