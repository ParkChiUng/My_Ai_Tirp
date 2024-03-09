package com.sessac.myaitrip.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.R
import com.sessac.myaitrip.databinding.FragmentToursBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 관광지 페이지
 */
class ToursFragment :
    ViewBindingBaseFragment<FragmentToursBinding>(FragmentToursBinding::inflate) {

    private lateinit var tourBinding: FragmentToursBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tourBinding = FragmentToursBinding.inflate(inflater, container, false)

        // 지역 탭 추가
        val tabLayoutArea = tourBinding.tabLayoutArea
        val areas = resources.getStringArray(R.array.areas)
        for (area in areas) {
            tabLayoutArea.addTab(tabLayoutArea.newTab().setText(area))
        }

        // 카테고리 탭 추가
        val tabLayoutCategory = tourBinding.tabLayoutCategory
        val categories = resources.getStringArray(R.array.categories)
        for (category in categories) {
            tabLayoutCategory.addTab(tabLayoutCategory.newTab().setText(category))
        }

        return tourBinding.root
    }
}