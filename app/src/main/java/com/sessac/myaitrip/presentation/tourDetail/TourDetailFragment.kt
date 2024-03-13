package com.sessac.myaitrip.presentation.tourDetail

import android.os.Build
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

    private var parcelableTourItem: TourItem? = null
    private lateinit var bottomNav: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parcelableTourItem = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("tourItem", TourItem::class.java)
        } else {
            arguments?.getParcelable("tourItem")
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        init()
    }

    private fun init() {
        removeBottomNav()
        setUpCollect()
        getTourApi()
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
                        val description =
                            state.data.items?.item?.mapNotNull { it.overview }.toString()

                        val sendText = if (description.contains("<br>")) {
                            Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            description
                        }
                        binding.tvDescription.setText(sendText)
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI HandleState", "${state.errorMessage}")
                        if (state.errorMessage == null)
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
                        state.data.totalCount
                        val images = state.data.items?.item?.mapNotNull { it.originImgUrl }
                        val adapter = ImageSliderAdapter(images)
                        binding.vpTour.adapter = adapter
                        binding.layoutIndicators.setViewPager(binding.vpTour)
                    }

                    is UiState.Error -> {
                        Log.d("TourAPI tourImgStatus error", "${state.errorMessage}")
                        if (state.errorMessage == null) {
                            parcelableTourItem?.let {
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
    }

    private fun getTourApi() {
        parcelableTourItem?.let {

            binding.tvTour.text = it.title

            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    launch {
                        tourDetailViewModel.getTourDetailFromAPI(it.contentId)
                    }

                    launch {
                        tourDetailViewModel.getTourImageFromAPI(it.contentId)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bottomNav.visibility = View.VISIBLE
    }
}