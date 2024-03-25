package com.sessac.myaitrip.presentation.tours

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.CONTENT_TYPE_ID_AREA
import com.sessac.myaitrip.common.CONTENT_TYPE_ID_CATEGORY
import com.sessac.myaitrip.common.TOUR_CONTENT_ID
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentToursBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.tours.adapter.ToursPagingAdapter
import com.sessac.myaitrip.util.repeatOnCreated
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 관광지 페이지
 */
class ToursFragment :
    ViewBindingBaseFragment<FragmentToursBinding>(FragmentToursBinding::inflate) {

    private lateinit var area: String
    private lateinit var category: String
    private var inputText: String? = null
    private lateinit var toursAdapter: ToursPagingAdapter
    private lateinit var toursRecyclerView: RecyclerView
    private lateinit var userId: String
    private lateinit var tourLikeList: MutableList<String>

    private val toursViewModel: ToursViewModel by viewModels() { ViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tourLikeList = mutableListOf()

        init()
    }

    private fun init() {
        setupTabLayout()
        setupSearch()
        getUserId()
        setupUserCollect()
    }

    /**
     * 유저 Id 조회
     */
    private fun getUserId() {
        toursViewModel.getUserPreferences()
    }

    /**
     * 유저 좋아요 조회
     */
    private fun getUserLikeList() {
        toursViewModel.getUserLikeListFromFireBase(userId)
    }

    private fun setupUserCollect() {
        /**
         * 유저 정보 조회
         */
        repeatOnCreated {
            toursViewModel.userPreferenceStatus.collectLatest { state ->
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
            toursViewModel.userLikeResult.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        tourLikeList = state.data
                        setupRecyclerviewAdapter()
                    }

                    is UiState.Error -> {
                        toursViewModel.getUserLikeListFromFireBase(userId)
                    }

                    else -> {}
                }
            }
        }
    }

    /**
     * 검색 레이아웃
     */
    private fun setupSearch() {
        val layoutSearch = binding.layoutSearch.searchInput

        layoutSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(inputResult: Editable) {
                inputText = inputResult.toString()
                setupRecyclerviewAdapter()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    /**
     * 탭 레이아웃
     */
    private fun setupTabLayout() {
        with(binding) {
            setupTab(tabLayoutArea, R.array.areas, CONTENT_TYPE_ID_AREA) { area = it }
            setupTab(tabLayoutCategory, R.array.categories, CONTENT_TYPE_ID_CATEGORY) { category = it }
        }
    }

    /**
     * 탭 레이아웃 이벤트 처리
     */
    private fun setupTab(
        tabLayout: TabLayout,
        arrayId: Int,
        contentTypeId: Map<String, String>,
        updateFunc: (String) -> Unit
    ) {
        val items = resources.getStringArray(arrayId)

        for (item in items) {
            tabLayout.addTab(tabLayout.newTab().setText(item))
        }

        /**
         * 초기 탭 설정
         * index 0 -> 서울, 관광지
         */
        updateFunc(contentTypeId[tabLayout.getTabAt(0)?.text].toString())

        /**
         * 탭 클릭 이벤트
         * 1. area or category 변경
         * 2. 리사이클러 뷰 포지션 0번째로 초기화
         * 3. 검색 text 초기화
         * 4. 관광지 리스트 조회
         */
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                updateFunc(contentTypeId[tab.text].toString())
                toursRecyclerView.scrollToPosition(0)
                binding.layoutSearch.searchInput.setText("")
                setupRecyclerviewAdapter()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                toursRecyclerView.scrollToPosition(0)
            }
        })
    }

    /**
     * 관광지 리스트 조회
     *
     * 지역명, 카테고리, 입력한 텍스트
     */
    private fun getTourList() {
        toursViewModel.getTourList(area, category, inputText.toString())
    }

    /**
     * 관광지 리스트 adapter에 sumbit
     */
    private fun setupTourListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            toursViewModel.tourStatus.collectLatest { pagingData ->
                toursAdapter.submitData(pagingData)
            }
        }
    }

    /**
     * 리사이클러 뷰 설정
     */
    private fun setupRecyclerviewAdapter() {
        toursRecyclerView = binding.rcvTours

        /**
         * 관광지 클릭 시
         */
        val itemOnClick: (TourItem) -> Unit = { tourItem ->
            val bundle = Bundle().apply {
                putString(TOUR_CONTENT_ID, tourItem.contentId)
            }
            findNavController().navigate(R.id.action_ToursFragment_to_TourDetailFragment, bundle)
        }

        /**
         * 관광지 좋아요 클릭 시
         *
         * 파이어 베이스에 업데이트 후 리스트 다시 가져온다
         */
        val likeOnClick: (AppCompatImageView, String) -> Unit = { likeImage, contentId ->
            if (likeImage.tag == "false") {
                likeImage.tag = "true"
                likeImage.setImageResource(R.drawable.ic_like_selected)
            } else {
                likeImage.tag = "false"
                likeImage.setImageResource(R.drawable.ic_like_white)
            }
            toursViewModel.updateUserLikeListFromFireBase(userId, contentId)
            getUserLikeList()
        }

        /**
         * 관광지 좋아요 상태
         */
        val liked: (AppCompatImageView) -> Unit = { likeImage ->
            likeImage.tag = "true"
            likeImage.setImageResource(R.drawable.ic_like_selected)
        }

        toursAdapter = ToursPagingAdapter(
            itemOnClick, likeOnClick, liked, tourLikeList,
            viewLifecycleOwner.lifecycleScope
        )

        toursRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        toursRecyclerView.adapter = toursAdapter

        getTourList()
        setupTourListCollect()
    }

    override fun onResume() {
        super.onResume()
        binding.layoutSearch.searchInput.setText("")
    }
}