package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentTourDetailBinding

/**
 * 관광지 상세 페이지
 */
class TourDetailFragment :
    ViewBindingBaseFragment<FragmentTourDetailBinding>(FragmentTourDetailBinding::inflate) {

    private lateinit var tourDetailBinding: FragmentTourDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tourDetailBinding = FragmentTourDetailBinding.inflate(inflater, container, false)
        return tourDetailBinding.root
    }
}