package com.sessac.myaitrip.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.sessac.myaitrip.BuildConfig
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

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var smallCardAdapter: SmallCardAdapter
    private lateinit var fullCardAdapter: FullCardAdapter
    private lateinit var popularRecyclerView: RecyclerView
    private lateinit var recommendRecyclerView: RecyclerView
    private lateinit var nearbyRecyclerView: RecyclerView

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // test data
        val testList = listOf("청계천", "강릉 경포대", "경포호(철새도래지)", "설악산 대청봉")
//        val testList = listOf("속초해변", "경포 호", "오죽헌", "설악산", "정선")

//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                homeViewModel.getTourListByTitle(testList)
//            }
//        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                geminiApi()
            }
        }

        setupRecyclerviewAdapter()
        setupCollect()
    }

    private suspend fun geminiApi(){
        val generativeModel = GenerativeModel(
            // For text-only input, use the gemini-pro model
            modelName = "gemini-pro",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = BuildConfig.GEMINI_API_KEY
        )

//        val prompt = "비오는 날에 방문하기 적합한 대한민국 인천의 관광지 포인트 20곳을 인기 순으로 나열. 결과는 {tour: [{ tourName: }]} json 형식으로 관광지 이름만 제공."

//        val prompt = "인천에서 가장 인기있는 관광지 포인트 20곳 나열. 결과는 {tour: [{ tourName: }]} json 형식으로 관광지 이름만 제공."

        val prompt = "인천에서 가장 인기있는 관광지 포인트 20곳 나열. 결과는 관광지 이름만 제공."

        val response = generativeModel.generateContent(prompt)

        Log.d("test", "gemini response : ${response.text}")

        // 숫자와 점 제거
        val cleanedInput = response.text!!.replace(Regex("\\d+\\."), "").trim()

        // 줄바꿈으로 분리
        val tourList = cleanedInput.split("\n").map { it.trim() }

        Log.d("test", "gemini response : $tourList")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.getTourListByTitle(tourList)
            }
        }
    }

    private fun setupRecyclerviewAdapter() {
        popularRecyclerView = homeBinding.rcvPopular
        recommendRecyclerView = homeBinding.rcvLocationRecommend
        nearbyRecyclerView = homeBinding.rcvNearbyRecommend

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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                homeViewModel.tourList.collect { tourList ->

                    fullCardAdapter.setTourList(tourList)
                    smallCardAdapter.setTourList(tourList)

                    for (i in tourList) {
                        Log.d("test", "tour list = $i")
                    }
                }
            }
        }
    }
}