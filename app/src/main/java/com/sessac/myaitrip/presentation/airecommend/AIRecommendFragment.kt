package com.sessac.myaitrip.presentation.airecommend

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.TOUR_CONTENT_ID
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentAiRecommendBinding
import com.sessac.myaitrip.presentation.common.CustomProgressLoadingDialog
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.util.repeatOnStarted
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

/**
 * AI 추천 페이지
 */
private const val RECOMMEND_RAINY_DAY = "흐린 날씨에 가기 좋은 대한민국 관광지를 추천해줘."
private const val RECOMMEND_SUNNY_DAY = "맑은 날씨에 가기 좋은 대한민국 관광지를 추천해줘."
private const val RECOMMEND_WEATHER_SPRING = "봄에 가기 좋은 대한민국 관광지를 추천해줘."
private const val RECOMMEND_WEATHER_SUMMER = "여름에 가기 좋은 대한민국 관광지를 추천해줘."
private const val RECOMMEND_WEATHER_FALL = "가을에 가기 좋은 대한민국 관광지를 추천해줘."
private const val RECOMMEND_WEATHER_WINTER = "겨울에 가기 좋은 대한민국 관광지를 추천해줘."

private const val ANSWER_FORMAT = """
            지역 이름은 (서울, 인천, 대전, 대구, 광주, 부산, 울산, 세종, 경기도, 강원도, 충청북도, 충청남도, 경상북도, 경상남도, 전라북도, 전라남도, 제주도)로 제한할게.
            답변은 지역 이름: 관광지 이름1, 관광지 이름 2, 관광지 이름3 ... 이런 형식으로 지켜서 답변해줘.
            
            다음과 같은 json 형식을 지켜서 답변해줘
            {
              "지역명": "관광지1, 관광지2, 관광지3",
              "지역명": "관광지1, 관광지2, 관광지3",
              "지역명": "관광지1, 관광지2, 관광지3",
              "지역명": "관광지1, 관광지2, 관광지3",
              "지역명": "관광지1, 관광지2, 관광지3",
              ...
            }
        """

class AIRecommendFragment :
    ViewBindingBaseFragment<FragmentAiRecommendBinding>(FragmentAiRecommendBinding::inflate) {

    private val aiRecommendViewModel: AIRecommendViewModel by viewModels { ViewModelFactory() }

    private lateinit var locationAdapter: AIRecommendLocationAdapter
    private lateinit var tourAdapter: AIRecommendTourAdapter

    private var recommendMap: Map<String, List<TourItem>>? = null

    private lateinit var progressLoadingDialog: CustomProgressLoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressLoadingDialog = CustomProgressLoadingDialog(requireContext())

        initLocationAdapter()
        initRecommendTourAdapter()

        setupRecommendResultStateCollection()
        getTourByGeminiWithPrompt(RECOMMEND_SUNNY_DAY) // 초기 실행 시 맑은 날 추천 데이터 가져오기

        setupCheckedChipStateListener()
    }

    private fun setupRecommendResultStateCollection() {
        repeatOnStarted {
            aiRecommendViewModel.aiResultStatus.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> {
                        // 프로그레스 다이얼로그
                        withContext(Dispatchers.Main) {
                            progressLoadingDialog.showDialog()
                        }
                    }
                    is UiState.Error -> {}
                    is UiState.Success -> {
                        recommendMap = state.data  // key: 지역, value: 지역별 추천 관광지 목록

                        withContext(Dispatchers.Main) {

                            // 현재 선택된 지역 + 현재 날씨
                            recommendMap?.let { recommendMap ->
                                locationAdapter.setAreaList(recommendMap.keys.toList())

                                recommendMap[recommendMap.keys.first()]?.let {
                                    Log.e("GeminiTourList", "GeminiTourList = $it")
                                    tourAdapter.setTourList(it)
                                }
                            }

                            progressLoadingDialog.dismissDialog()   // Loading Dialog 제거
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setupCheckedChipStateListener() {
        with(binding) {
            chipgroupAiRecommend.setOnCheckedStateChangeListener { chipGroup, _ ->
                chipGroup.findViewById<Chip>(chipGroup.checkedChipId)?.let { selectedChip ->
                    val selectedText = selectedChip.text.toString()

                    when (selectedText) {
                        resources.getString(R.string.sunny) -> getTourByGeminiWithPrompt(RECOMMEND_SUNNY_DAY)
                        resources.getString(R.string.rainy) -> getTourByGeminiWithPrompt(RECOMMEND_RAINY_DAY)
                        resources.getString(R.string.spring) -> getTourByGeminiWithPrompt(RECOMMEND_WEATHER_SPRING)
                        resources.getString(R.string.summer) -> getTourByGeminiWithPrompt(RECOMMEND_WEATHER_SUMMER)
                        resources.getString(R.string.fall) -> getTourByGeminiWithPrompt(RECOMMEND_WEATHER_FALL)
                        resources.getString(R.string.winter) -> getTourByGeminiWithPrompt(RECOMMEND_WEATHER_WINTER)
                        else -> {}
                    }
                }
            }
        }
    }

    private fun getTourByGeminiWithPrompt(prompt: String) {
        aiRecommendViewModel.getTourByGeminiAiWithPrompt(
            """
            $prompt
            $ANSWER_FORMAT
            """.trimIndent()
        )
    }

    private fun initLocationAdapter() {
        with(binding) {
            locationAdapter = AIRecommendLocationAdapter(
                scope = viewLifecycleOwner.lifecycleScope,
                onItemClick = { area ->
                    recommendMap?.let { recommendMap ->
                        recommendMap[area]?.let { tourList ->
                            Log.e("recommendAI", "recommendTourList = $tourList")
                            tourAdapter.setTourList(tourList)
                        }
                    }
                }
            ).also {
                rvAiRecommendLocation.adapter = it
            }
        }
    }

    private fun initRecommendTourAdapter() {
        with(binding) {
            tourAdapter = AIRecommendTourAdapter(
                scope = viewLifecycleOwner.lifecycleScope,
                onItemClick = { tourItem ->
                    // 관광지 상세로 이동
                    Bundle().apply {
                        putString(TOUR_CONTENT_ID, tourItem.contentId)
                        findNavController().navigate(R.id.action_AIRecommend_to_TourDetail, this)
                    }
                }
            ).also {
                rvAiRecommendTour.adapter = it
            }
        }
    }
}