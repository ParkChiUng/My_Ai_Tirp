package com.sessac.myaitrip.presentation.diary

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.TOUR_IMAGE_LIST
import com.sessac.myaitrip.common.TOUR_ITEM
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentDiaryBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.diary.adapter.ImageCardAdapter
import com.sessac.myaitrip.presentation.tourDetail.adapter.ImageSliderAdapter

/**
 * 다이어리 페이지
 */
class DiaryFragment :
    ViewBindingBaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::inflate) {

    private val diaryViewModel: DiaryViewModel by viewModels() { ViewModelFactory(requireContext()) }
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var userImageRecyclerView: RecyclerView
    private lateinit var userImageAdapter: ImageCardAdapter
    private var tourImageList: List<String>? = null
    private var parcelableTourItem: TourItem? = null
    private var userImageList: MutableList<Uri> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parcelableTourItem = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TOUR_ITEM, TourItem::class.java)
        } else {
            arguments?.getParcelable(TOUR_ITEM)
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        init()
    }

    private fun init(){
        removeBottomNav()
        getTourItem()
        clickEventHandler()
        setupRecyclerviewAdapter()
    }

    private fun clickEventHandler(){
        with(binding){
            btnAddImg.throttleClick().bind {
                navigateGallery()
            }
        }
    }

    private fun navigateGallery() {
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
//            addCategory(Intent.CATEGORY_OPENABLE)
//            type = "image/*"
//            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
//        }
//        imageResult.launch(intent)
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        imageResult.launch(intent)
    }

    private val imageResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val clipData = result.data?.clipData
            val imageUri = result.data?.data

            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    userImageList.add(uri)
                }
            } else if (imageUri != null) {
                userImageList.add(imageUri)
            }

            userImageAdapter.updateImages(userImageList)
        }
    }

    private fun getTourItem(){
        tourImageList = arguments?.getStringArrayList(TOUR_IMAGE_LIST)

        with(binding){
            parcelableTourItem?.let {
                tvTourName.setText(it.title)
                tvTourAddress.setText(it.address)
                imageSliderAdapter = ImageSliderAdapter(tourImageList)
                vpTour.adapter = imageSliderAdapter
                layoutIndicators.setViewPager(vpTour)
            }
        }
    }

    private fun setupRecyclerviewAdapter(){
        userImageRecyclerView = binding.rcvImg
        userImageAdapter = ImageCardAdapter(scope = viewLifecycleOwner.lifecycleScope)
        userImageRecyclerView.adapter = userImageAdapter
    }

    private fun removeBottomNav() {
        bottomNav = requireActivity().findViewById(R.id.bnv_home)
        bottomNav.visibility = View.GONE
    }
    override fun onDestroy() {
        super.onDestroy()
        bottomNav.visibility = View.VISIBLE
    }
}