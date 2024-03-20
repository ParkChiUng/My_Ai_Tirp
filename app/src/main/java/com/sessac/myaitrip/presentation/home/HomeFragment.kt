package com.sessac.myaitrip.presentation.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.databinding.FragmentHomeBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.home.adapter.FullCardAdapter
import com.sessac.myaitrip.presentation.home.adapter.SmallCardAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 홈
 */
class HomeFragment :
    ViewBindingBaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var recommendRecyclerView: RecyclerView
    private lateinit var nearbyRecyclerView: RecyclerView

    private lateinit var popularAdapter: FullCardAdapter
    private lateinit var recommendAdapter: SmallCardAdapter
    private lateinit var nearbyAdapter: SmallCardAdapter
    private lateinit var contentIdList: List<String>
    private lateinit var areaList: Array<String>
    private lateinit var cityName: String
    private lateinit var listType: HomeViewModel.ListType

    private val homeViewModel: HomeViewModel by viewModels() { ViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        areaList = resources.getStringArray(R.array.areas_home)
        cityName = areaList[0]

        init()
        setupRecyclerviewAdapter()
        setupCollect()
        clickEventHandler()
    }

    private fun init() {
        getPopularTourList()
        getAreaRecommendTourList()
    }

    private fun clickEventHandler() {

        /**
         * 지역 별 추천 관광지 chip 클릭 리스너
         */
        with(binding) {
            chipGroup.setOnCheckedStateChangeListener { group, _ ->
                cityName = when (group.checkedChipId) {
                    chipSeoul.id -> areaList[0]
                    chipIncheon.id -> areaList[1]
                    chipGyeonggi.id -> areaList[2]
                    chipGangwon.id -> areaList[3]
                    chipChungcheong.id -> areaList[4]
                    chipJeolla.id -> areaList[5]
                    chipGyeongsang.id -> areaList[6]
                    chipJeju.id -> areaList[7]
                    else -> areaList[0]
                }
                getAreaRecommendTourList()
            }
        }
    }

    /**
     * 인기 관광지 조회
     */
    private fun getPopularTourList() {
        listType = HomeViewModel.ListType.POPULAR
        homeViewModel.getPopularTourListFromFireBase(listType.toString())
    }

    /**
     * 지역 별 추천 관광지 조회
     */
    private fun getAreaRecommendTourList() {
        listType = HomeViewModel.ListType.AREA_RECOMMEND
        homeViewModel.getAreaRecommendTourListFromFireBase(listType.toString(), cityName)
    }

    private fun setupCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    homeViewModel.popularTourList.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                popularAdapter.setTourList(state.data)
                                popularRecyclerView.scrollToPosition(0)
                            }

                            is UiState.Error -> {
                                Log.e("TourAPI HandleState", "${state.errorMessage}")
                            }

                            is UiState.Loading -> {
                                Log.e("TourAPI HandleState", " loading : $$state")
                            }

                            else -> {
                                Log.e("TourAPI HandleState", " else : $state")
                            }
                        }
                    }
                }

                launch {
                    homeViewModel.areaRecommendTourList.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                recommendAdapter.setTourList(state.data)
                                recommendRecyclerView.scrollToPosition(0)
                            }

                            is UiState.Error -> {

                            }

                            is UiState.Loading -> {

                            }

                            else -> {}
                        }
                    }
                }

                launch {
                    homeViewModel.nearbyTourList.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                nearbyAdapter.setTourList(state.data)
                                nearbyRecyclerView.scrollToPosition(0)
                            }

                            is UiState.Error -> {

                            }

                            is UiState.Loading -> {

                            }

                            else -> {}
                        }
                    }
                }

                launch {
                    homeViewModel.fireBaseResult.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                contentIdList = state.data["contentIdList"] as List<String>
                                listType = HomeViewModel.ListType.valueOf(state.data["listType"] as String)

                                viewLifecycleOwner.lifecycleScope.launch {
                                    repeatOnLifecycle(Lifecycle.State.CREATED) {
                                        homeViewModel.getTourListByContentId(contentIdList, listType)
                                    }
                                }
                            }

                            is UiState.Loading -> {

                            }

                            is UiState.Error -> {
                                Log.e("TourAPI HandleState", "${state.errorMessage}")
                            }

                            else -> {
                                Log.e("TourAPI HandleState", "$state")
                            }
                        }
                    }
                }
            }
        }

        /**
         * fireBase에 추천 관광지 데이터 insert
         */
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                homeViewModel.tourList.collect { tourList ->
//                    for (item in tourList) {
//                        Firebase.firestore.collection("tour").document("data")
//                            .collection("recommend").document(cityName)
//                            .update("contentId", FieldValue.arrayUnion(item.contentId))
//                            .addOnSuccessListener {
//                                Log.d("TAG", "DocumentSnapshot successfully updated!")
//                            }
//                            .addOnFailureListener { e ->
//                                Log.w("TAG", "Error updating document", e)
//                            }
//                    }
//                }
//            }
//        }
    }

    /**
     * 각 리사이클러 뷰 설정
     */
    private fun setupRecyclerviewAdapter() {
        popularRecyclerView = binding.rcvPopular
        recommendRecyclerView = binding.rcvLocationRecommend
        nearbyRecyclerView = binding.rcvNearbyRecommend

        popularAdapter = FullCardAdapter({ tourItem ->
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)

        recommendAdapter = SmallCardAdapter({ tourItem ->
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)

        nearbyAdapter = SmallCardAdapter({ tourItem ->
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)


        popularRecyclerView.adapter = popularAdapter
        recommendRecyclerView.adapter = recommendAdapter
        nearbyRecyclerView.adapter = nearbyAdapter
    }

    /**
     * gemini api를 통해 가져온 관광지 정보로 fireBase의 지역 별 추천 관광지에 insert한다.
     */
//    private fun insertFireBase() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                homeViewModel.geminiApi(getString(R.string.recommend_popular, cityName))
//            }
//        }
//    }
}