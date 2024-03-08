package com.sessac.myaitrip.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentTourMapBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 맵 페이지
 */
class TourMapFragment :
    ViewBindingBaseFragment<FragmentTourMapBinding>(FragmentTourMapBinding::inflate) {

    private lateinit var mapBinding: FragmentTourMapBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mapBinding = FragmentTourMapBinding.inflate(inflater, container, false)
        return mapBinding.root
    }
}