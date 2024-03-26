package com.sessac.myaitrip.presentation.tourmap

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.naver.maps.map.overlay.Marker
import com.sessac.myaitrip.R
import com.sessac.myaitrip.data.entities.TourClusterItemData
import com.sessac.myaitrip.data.entities.TourClusterItemKey
import com.sessac.myaitrip.databinding.FragmentTourMapDetailBottomSheetBinding
import com.sessac.myaitrip.presentation.common.TourDetailBottomSheetViewModel
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.util.GlideUtil
import com.sessac.myaitrip.util.repeatOnCreated
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class TourDetailBottomSheetFragment(
    private val tourKey: TourClusterItemKey,
    private val tourData: TourClusterItemData,
    private val positionMarker: Marker? = null,
    val itemClick: () -> Unit
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        positionMarker?.map = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewCountCollection()  // 조회수 Collector
        tourDetailBottomSheetViewModel.getTourViewCount(tourKey.contentId)  // 조회수 가져오기

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


            root.clicks().onEach {
                itemClick()

                dismiss()
            }.launchIn(lifecycleScope)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}