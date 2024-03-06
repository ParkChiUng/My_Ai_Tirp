package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentAiRecommendBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * AI 추천 페이지
 */
class AIRecommendFragment :
    ViewBindingBaseFragment<FragmentAiRecommendBinding>(FragmentAiRecommendBinding::inflate) {

    private lateinit var aiBinding: FragmentAiRecommendBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        aiBinding = FragmentAiRecommendBinding.inflate(inflater, container, false)
        return aiBinding.root
    }
}