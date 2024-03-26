package com.sessac.myaitrip.presentation.tourDetail

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.TOUR_CONTENT_ID
import com.sessac.myaitrip.common.TOUR_IMAGE_LIST
import com.sessac.myaitrip.common.TOUR_ITEM
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentTourDetailBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.tourDetail.adapter.ImageSliderAdapter
import com.sessac.myaitrip.util.repeatOnCreated
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 관광지 상세 페이지
 */
class TourDetailFragment :
    ViewBindingBaseFragment<FragmentTourDetailBinding>(FragmentTourDetailBinding::inflate) {

    private val tourDetailViewModel: TourDetailViewModel by viewModels {
        ViewModelFactory()
    }

    private var tourItem: TourItem? = null
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var tourContentId: String
    private lateinit var bundle: Bundle
    private lateinit var userId: String
    private lateinit var tourLikeList: MutableList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tourContentId = arguments?.getString(TOUR_CONTENT_ID).toString()
        tourLikeList = mutableListOf()

        binding.toolbarTourDetail.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        init()
    }

    private fun init() {
        removeBottomNav()
        setUpCollect()
        getTourApi()
        clickEventHandler()
        getUserId()
        setupUserCollect()
    }

    /**
     * 유저 Id 조회
     */
    private fun getUserId() {
        tourDetailViewModel.getUserPreferences()
    }

    /**
     * 유저 좋아요 조회
     */
    private fun getUserLikeList() {
        tourDetailViewModel.getUserLikeListFromFireBase(userId)
    }

    private fun setupUserCollect() {
        /**
         * 유저 정보 조회
         */
        repeatOnCreated {
            tourDetailViewModel.userPreferenceStatus.collectLatest { state ->
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
    }

    private fun setUpFireBaseCollect() {
        /**
         * FireBase 유저 좋아요 관광지 리스트 조회
         */
        viewLifecycleOwner.lifecycleScope.launch {
            tourDetailViewModel.userLikeResult.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        tourLikeList = state.data

                        if (tourLikeList.contains(tourContentId)) {
                            with(binding) {
                                ivLike.tag = "true"
                                ivLike.setImageResource(R.drawable.ic_like_selected)
                            }
                        }
                    }

                    is UiState.Error -> {
                        tourDetailViewModel.getUserLikeListFromFireBase(userId)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun clickEventHandler() {
        with(binding) {
            btnDiary.throttleClick().bind {
                findNavController().navigate(R.id.action_TourDetailFragment_to_DiaryFragment, bundle)
            }

            // 지도로 보기 버튼
            btnMap.throttleClick().bind {
                moveToMap()
            }

            ivLike.throttleClick().bind {
                ivLike.tag = if (ivLike.tag == "false") "true" else "false"
                ivLike.setImageResource(if (ivLike.tag == "true") R.drawable.ic_like_selected else R.drawable.ic_like_white)
                tourDetailViewModel.updateUserLikeListFromFireBase(userId, tourContentId)
            }
        }
    }

    /**
     * Move to map
     * 지도로 보기 화면으로 이동
     */
    private fun moveToMap() {
        Intent(requireContext(), TourDetailByMapActivity::class.java).apply {
            tourItem?.let {
                putExtra("tourItem", it)
            }
            startActivity(this)
        }
    }

    private fun removeBottomNav() {
        bottomNav = requireActivity().findViewById(R.id.bnv_home)
        bottomNav.visibility = View.GONE
    }

    private fun setUpCollect() {
        lifecycleScope.launch {
            tourDetailViewModel.tourDetailStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val response = state.data.response.body
                        val description = response.items?.item?.mapNotNull { it.overview }.toString().replace("[","").replace("]","")

                        Log.e("tourDetail", description)

                        val sendText = if (description.contains("<br>")) {
                            Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            description
                        }
                        binding.tvTourDetailDescription.text = sendText
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI HandleState", "${state.errorMessage}")
                        if (state.errorMessage == "null")
                            binding.tvTourDetailDescription.setText(R.string.tour_no_detail)
                        else
                            getTourApi()
                    }

                    else -> {
                        Log.d("TourAPI HandleState", "$state")
                        binding.tvTourDetailDescription.setText(R.string.tour_no_detail)
                    }
                }
            }
        }

        lifecycleScope.launch {
            tourDetailViewModel.tourImgStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val response = state.data.response.body
                        val images = response.items?.item?.mapNotNull { it.originImgUrl }
                        images?.let {
                            val adapter = ImageSliderAdapter(it)
                            binding.vpTour.adapter = adapter
                            binding.layoutIndicators.setViewPager(binding.vpTour)

                            bundle = Bundle().apply {
                                putStringArrayList(TOUR_IMAGE_LIST, ArrayList(it))
                                putParcelable(TOUR_ITEM, tourItem)
                            }
                        }
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI tourImgStatus error", "${state.errorMessage}")
                        if (state.errorMessage == null) {
                            tourItem?.let {
                                val images = listOf(it.firstImage)
                                val adapter = ImageSliderAdapter(images)
                                binding.vpTour.adapter = adapter

                                bundle = Bundle().apply {
                                    putStringArrayList(TOUR_IMAGE_LIST, ArrayList(images))
                                    putParcelable(TOUR_ITEM, it)
                                }
                            }
                        } else
                            getTourApi()
                    }

                    else -> {
                        Log.d("TourAPI tourImgStatus else ", "$state")
                    }
                }
            }
        }

        lifecycleScope.launch {
            tourDetailViewModel.tourStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        tourItem = state.data
                        tourItem?.let { tourItem ->
                            with(binding) {
                                tvTour.text = tourItem.title
                                tvTourDetailAddress.text = if(tourItem.address2.isBlank()) {
                                    tourItem.address
                                } else {
                                    tourItem.address + "\n" + tourItem.address2
                                }
                            }
                        }

                        addCountingFromFireBase()
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI tourStatus error", "${state.errorMessage}")
                    }

                    else -> {
                        Log.d("TourAPI tourStatus else ", "$state")
                    }
                }
            }
        }
    }

    private fun getTourApi() {
        repeatOnCreated {
            launch {
                tourDetailViewModel.getTourFromRoom(tourContentId)
            }

            launch {
                tourDetailViewModel.getTourDetailFromAPI(tourContentId)
            }

            launch {
                tourDetailViewModel.getTourImageFromAPI(tourContentId)
            }
        }
    }

    private fun addCountingFromFireBase() {
        tourItem?.let {
            viewLifecycleOwner.lifecycleScope.launch {
                launch {
                    tourDetailViewModel.addCountingFromFireBase(it.contentId)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomNav.visibility = View.VISIBLE
    }
}