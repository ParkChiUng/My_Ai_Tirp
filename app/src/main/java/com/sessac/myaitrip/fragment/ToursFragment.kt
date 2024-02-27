package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentToursBinding

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
        return tourBinding.root
    }
}