package com.sessac.myaitrip.presentation.tourDetail

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 관광지 상세 페이지
 */
class TourDetailFragment :
    ViewBindingBaseFragment<FragmentTourDetailBinding>(FragmentTourDetailBinding::inflate) {

    private val tourDetailViewModel: TourDetailViewModel by viewModels {
        ViewModelFactory(
            requireContext()
        )
    }

    private var tourItem: TourItem? = null
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var tourContentId: String
    private lateinit var bundle: Bundle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tourContentId = arguments?.getString(TOUR_CONTENT_ID).toString()

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        init()
    }

    private fun init() {
        removeBottomNav()
        setUpCollect()
        getTourApi()
        addCountingFromFireBase()
        clickEventHandler()
    }

    private fun clickEventHandler(){
        with(binding){
            btnDiary.throttleClick().bind {
                findNavController().navigate(R.id.action_TourDetailFragment_to_DiaryFragment, bundle)
            }
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
                        val description =
                            response.items?.item?.mapNotNull { it.overview }.toString()

                        val sendText = if (description.contains("<br>")) {
                            Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            description
                        }
                        binding.tvDescription.setText(sendText)
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI HandleState", "${state.errorMessage}")
                        if (state.errorMessage == "null")
                            binding.tvDescription.setText(R.string.tour_no_detail)
                        else
                            getTourApi()
                    }

                    else -> {
                        Log.d("TourAPI HandleState", "$state")
                        binding.tvDescription.setText(R.string.tour_no_detail)
                    }
                }
            }
        }

        lifecycleScope.launch {
            tourDetailViewModel.tourImgStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val response = state.data.response.body
                        val images = response.items?.item?.mapNotNull { it.originImgUrl}
                        val adapter = ImageSliderAdapter(images)
                        binding.vpTour.adapter = adapter
                        binding.layoutIndicators.setViewPager(binding.vpTour)

                        bundle = Bundle().apply {
                            putStringArrayList(TOUR_IMAGE_LIST, images?.let { ArrayList(it) })
                            putParcelable(TOUR_ITEM, tourItem)
                        }
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI tourImgStatus error", "${state.errorMessage}")
                        if (state.errorMessage == null) {
                            tourItem?.let {
                                val images = listOf(it.firstImage as String)
                                val adapter = ImageSliderAdapter(images)
                                binding.vpTour.adapter = adapter
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
                        binding.tvTour.text = tourItem?.title
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
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