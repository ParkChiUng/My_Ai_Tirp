package com.sessac.myaitrip.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.sessac.myaitrip.R
import com.sessac.myaitrip.databinding.FragmentHomeBinding
import com.sessac.myaitrip.home.adapter.FullCardAdapter
import com.sessac.myaitrip.home.adapter.SmallCardAdapter
import com.sessac.myaitrip.home.viewmodels.HomeViewModel
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import kotlinx.coroutines.launch

/**
 * 홈
 */
class HomeFragment :
    ViewBindingBaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private lateinit var smallCardAdapter: SmallCardAdapter
    private lateinit var fullCardAdapter: FullCardAdapter
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var recommendRecyclerView: RecyclerView
    private lateinit var nearbyRecyclerView: RecyclerView
    private var cityName = "서울"

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setupRecyclerviewAdapter()
        setupCollect()
        clickEventHandler()
    }

    private fun init() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.geminiApi(getString(R.string.recommend_popular, cityName))
            }
        }
    }

    private fun clickEventHandler() {
        with(binding) {
            chipGroup.setOnCheckedStateChangeListener { group, _ ->
                cityName = when (group.checkedChipId) {
                    chipSeoul.id -> "서울"
                    chipIncheon.id -> "인천"
                    chipGyeonggi.id -> "경기도"
                    chipGangwon.id -> "강원도"
                    chipChungcheong.id -> "충청도"
                    chipJeolla.id -> "전라도"
                    chipGyeongsang.id -> "경상도"
                    chipJeju.id -> "제주"
                    else -> "서울"
//                    chipSeoul.id -> chipSeoul.text.toString()
//                    chipIncheon.id -> chipIncheon.text.toString()
//                    chipGyeonggi.id -> chipGyeonggi.text.toString()
//                    chipGangwon.id -> chipGangwon.text.toString()
//                    chipChungcheong.id -> chipChungcheong.text.toString()
//                    chipJeolla.id -> chipJeolla.text.toString()
//                    chipGyeongsang.id -> chipGyeongsang.text.toString()
//                    else -> chipSeoul.text.toString()
                }
                init()
            }
        }
    }

    private fun setupRecyclerviewAdapter() {
        popularRecyclerView = binding.rcvPopular
        recommendRecyclerView = binding.rcvLocationRecommend
        nearbyRecyclerView = binding.rcvNearbyRecommend

        smallCardAdapter = SmallCardAdapter({ tourItem ->
            Log.d("test", "tour click item = $tourItem")
        }, viewLifecycleOwner.lifecycleScope)

        fullCardAdapter = FullCardAdapter({ tourItem ->
            Log.d("test", "tour click item = $tourItem")
        }, viewLifecycleOwner.lifecycleScope)

        popularRecyclerView.adapter = fullCardAdapter
        recommendRecyclerView.adapter = smallCardAdapter
        nearbyRecyclerView.adapter = smallCardAdapter
    }

    private fun setupCollect() {
        Firebase.firestore.collection("tour").document("data").collection("recommend")
            .document(cityName)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.CREATED) {
//                            for (i in document.data){
//
//                            }
//                            val contentId = listOf<String>(document.data)

                            Log.d("TAG", "document : $document")
                            Log.d("TAG", "document.id : ${document.id}")
                            Log.d("TAG", "document.data : ${document.data}")

//                            val contentIdList = document.data?.get("contentId") as List<String>

//                            Log.d("TAG", "document.data : $contentIdList")

//                            homeViewModel.getTourListById(contentIdList)

//                            val contentIdList =
                            insertFireBase()
                        }
                    }

                } else {
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }
    }

    private fun insertFireBase() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.tourList.collect { tourList ->

//                    fullCardAdapter.setTourList(tourList)
//                    smallCardAdapter.setTourList(tourList)
//                    recommendRecyclerView.scrollToPosition(0)
                    for (item in tourList) {
                        Firebase.firestore.collection("tour").document("data")
                            .collection("recommend").document(cityName)
                            //                        .set(data, SetOptions.merge())
                            .update("contentId", FieldValue.arrayUnion(item.contentId))
//                        .update("contentId", tourList.map { it.contentId })
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
}