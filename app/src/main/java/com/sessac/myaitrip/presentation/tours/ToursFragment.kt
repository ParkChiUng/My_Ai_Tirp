package com.sessac.myaitrip.presentation.tours

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.CONTENT_TYPE_ID_AREA
import com.sessac.myaitrip.common.CONTENT_TYPE_ID_CATEGORY
import com.sessac.myaitrip.databinding.FragmentToursBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.tours.adapter.ToursPagingAdapter
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

    private val toursViewModel: ToursViewModel by viewModels() { ViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTabLayout()
        setupRecyclerviewAdapter()
        getTourList()
        setupCollect()
        setupSearch()
    }

    private fun setupSearch() {
        val layoutSearch = binding.layoutSearch.searchInput

        layoutSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(inputResult: Editable) {
                inputText = inputResult.toString()
                getTourList()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupTabLayout() {
        with(binding) {
            setupTab(tabLayoutArea, R.array.areas, CONTENT_TYPE_ID_AREA) { area = it }
            setupTab(tabLayoutCategory, R.array.categories, CONTENT_TYPE_ID_CATEGORY) {
                category = it
            }
        }
    }

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
                getTourList()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                getTourList()
            }
        })
    }

    private fun getTourList() {
        toursViewModel.getTourList(area, category, inputText.toString())
    }

    private fun setupCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    toursViewModel.tourStatus.collectLatest { pagingData ->
                        toursAdapter.submitData(pagingData)
                    }
                }
            }
        }
    }

    /**
     * 리사이클러 뷰 설정
     */
    private fun setupRecyclerviewAdapter() {
        toursRecyclerView = binding.rcvTours

        toursAdapter = ToursPagingAdapter({ tourItem ->
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_ToursFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)

        toursRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        toursRecyclerView.adapter = toursAdapter
    }
}