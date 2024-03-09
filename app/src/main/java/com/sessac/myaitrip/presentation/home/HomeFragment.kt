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
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.sessac.myaitrip.R
import com.sessac.myaitrip.databinding.FragmentHomeBinding
import com.sessac.myaitrip.presentation.home.adapter.FullCardAdapter
import com.sessac.myaitrip.presentation.home.adapter.SmallCardAdapter
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
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

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        areaList = resources.getStringArray(R.array.areas)
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

    private fun getPopularTourList(){
        Firebase.firestore.collection("tour").document("data").collection("popular")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    /**
                     * popular의 모든 리스트를 가져오고 counting 높은 순으로 5개만 정렬
                     *
                     * it.first -> contentId
                     * it.second -> counting
                     *
                     */
                    contentIdList = document.documents.map { doc ->
                        Pair(doc.id, doc.getLong("counting") ?: 0)
                    }.sortedByDescending { it.second }
                        .take(5)
                        .map{ it.first }

                    viewLifecycleOwner.lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                            homeViewModel.getTourListByContentId(contentIdList,
                                HomeViewModel.ListType.POPULAR
                            )
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun getAreaRecommendTourList(){
        Firebase.firestore.collection("tour").document("data").collection("recommend")
            .document(cityName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    contentIdList = (document.data?.get("contentId") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                    viewLifecycleOwner.lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
                            homeViewModel.getTourListByContentId(contentIdList,
                                HomeViewModel.ListType.AREA_RECOMMEND
                            )
                        }
                    }
                } else {
                    insertFireBase()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun setupCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    homeViewModel.popularTourList.collect { tourList ->
                        popularAdapter.setTourList(tourList)
                        popularRecyclerView.scrollToPosition(0)
                    }
                }

                launch {
                    homeViewModel.areaRecommendTourList.collect { tourList ->
                        recommendAdapter.setTourList(tourList)
                        recommendRecyclerView.scrollToPosition(0)
                    }
                }
                launch {
                    homeViewModel.nearbyTourList.collect { tourList ->
                        nearbyAdapter.setTourList(tourList)
                        nearbyRecyclerView.scrollToPosition(0)
                    }
                }
            }
        }

        /**
         * fireBase에 추천 관광지 데이터 insert
         */
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.tourList.collect { tourList ->
                    for (item in tourList) {
                        Firebase.firestore.collection("tour").document("data")
                            .collection("recommend").document(cityName)
                            .update("contentId", FieldValue.arrayUnion(item.contentId))
                            .addOnSuccessListener {
                                Log.d("TAG", "DocumentSnapshot successfully updated!")
                            }
                            .addOnFailureListener { e ->
                                Log.w("TAG", "Error updating document", e)
                            }
                    }
                }
            }
        }
    }

    /**
     * gemini api를 통해 가져온 관광지 정보로 fireBase의 지역 별 추천 관광지에 insert한다.
     */
    private fun insertFireBase() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.geminiApi(getString(R.string.recommend_popular, cityName))
            }
        }
    }

    /**
     * 각 리사이클러 뷰 설정
     */
    private fun setupRecyclerviewAdapter() {
        popularRecyclerView = binding.rcvPopular
        recommendRecyclerView = binding.rcvLocationRecommend
        nearbyRecyclerView = binding.rcvNearbyRecommend

        popularAdapter = FullCardAdapter({ tourItem ->
            Log.d("test", "tour click item = $tourItem")
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)

        recommendAdapter = SmallCardAdapter({ tourItem ->
            Log.d("test", "tour click item = $tourItem")
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)

        nearbyAdapter = SmallCardAdapter({ tourItem ->
            Log.d("test", "tour click item = $tourItem")
            val bundle = Bundle().apply {
                putParcelable("tourItem", tourItem)
            }
            findNavController().navigate(R.id.action_HomeFragment_to_TourDetailFragment, bundle)
        }, viewLifecycleOwner.lifecycleScope)


        popularRecyclerView.adapter = popularAdapter
        recommendRecyclerView.adapter = recommendAdapter
        nearbyRecyclerView.adapter = nearbyAdapter
    }
}