package com.sessac.myaitrip.presentation.diary

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.TOUR_IMAGE_LIST
import com.sessac.myaitrip.common.TOUR_ITEM
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentDiaryBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.diary.adapter.ImageCardAdapter
import com.sessac.myaitrip.presentation.tourDetail.adapter.ImageSliderAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.sessac.myaitrip.util.showToast

/**
 * 다이어리 페이지
 */
class DiaryFragment :
    ViewBindingBaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::inflate) {

    private val diaryViewModel: DiaryViewModel by viewModels() { ViewModelFactory() }
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var userImageRecyclerView: RecyclerView
    private lateinit var userImageAdapter: ImageCardAdapter
    private var tourImageList: List<String>? = null
    private var parcelableTourItem: TourItem? = null
    private var userImageList: MutableList<Uri> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * detail 페이지에서 parcelable로 tourItem 가져온다.
         */
        parcelableTourItem = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(TOUR_ITEM, TourItem::class.java)
        } else {
            arguments?.getParcelable(TOUR_ITEM)
        }

        init()
    }

    private fun init() {
        removeBottomNav()
        getTourItem()
        clickEventHandler()
        setupRecyclerviewAdapter()
        setUpCollect()
    }

    /**
     * 클릭 이벤트 핸들러
     *
     * 1. 이미지 add 버튼 클릭 시 갤러리 열림
     * 2. 등록하기 버튼 클릭 시 다이어리 유효성 체크 후 userId 가져옴
     * 3. 툴바 뒤로가기 클릭 이벤트
     *
     */
    private fun clickEventHandler() {
        with(binding) {
            btnAddImg.throttleClick().bind {
                navigateGallery()
            }
            btnSubmit.throttleClick().bind {
                if (isValid()) diaryViewModel.getUserPreferences()
            }
            toolbar.throttleClick().bind {
                findNavController().popBackStack()
            }
        }
    }

    /**
     * [유효성 체크]
     * 패키지 등록 버튼 클릭 시 호출
     */
    private fun isValid(): Boolean {
        var check = true
        with(binding) {
            if (etDiaryTitle.text.toString() == "") {
                requireContext().showToast("다이어리 제목을 입력해주세요.")
                check = false
            }
            if (etDiaryReview.text.toString() == "") {
                requireContext().showToast("다이어리 내용을 입력해주세요.")
                check = false
            }
        }
        return check
    }

    /**
     * 갤러리 호출
     */
    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        imageResult.launch(intent)
    }

    /**
     * 선택한 이미지가 다중인지 단일인지 판별 후 adpater에 이미지 등록
     */
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

    /**
     * 1. detail 페이지에서 받은 관광지 이미지 보여준다.
     * 2. detail 페이지에서 받은 관광지 이름, 주소를 보여준다.
     */
    private fun getTourItem() {
        tourImageList = arguments?.getStringArrayList(TOUR_IMAGE_LIST)

        with(binding) {
            parcelableTourItem?.let {
                tvTourName.setText(it.title)
                tvTourAddress.setText(it.address)
            }

            tourImageList?.let {
                imageSliderAdapter = ImageSliderAdapter(it)
                vpTour.adapter = imageSliderAdapter
                layoutIndicators.setViewPager(vpTour)
            }
        }
    }

    private fun setUpCollect() {

        /**
         * userId를 수신하면  userId와 DiaryItem을 FireBase에 저장.
         */
        lifecycleScope.launch {
            diaryViewModel.userPreferenceStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val userId = state.data.userId

                        val diaryItem = DiaryItem(
                            contentId = parcelableTourItem?.contentId.toString(),
                            diaryTitle = binding.etDiaryTitle.text.toString(),
                            diaryReview = binding.etDiaryReview.text.toString(),
                            diaryImage = userImageList
                        )

                        diaryViewModel.addDiaryFromFireBase(userId, diaryItem)

                    }

                    else -> {}
                }
            }
        }

        /**
         * stateFlow Collect
         *
         * 정상적으로 userId를 가져오면 UserId와 입력한 DiaryItem를 FireBase에 저장
         *
         */
        lifecycleScope.launch {
            diaryViewModel.fireBaseResult.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        requireContext().showToast("다이어리 저장 완료")
                        findNavController().popBackStack()
                    }

                    is UiState.Error -> {
                        requireContext().showToast("다이어리 저장 실패")
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setupRecyclerviewAdapter() {
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