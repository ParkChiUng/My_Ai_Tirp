package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentNearbyToursBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 내 주변 관광지 페이지
 */
class NearbyToursFragment :
    ViewBindingBaseFragment<FragmentNearbyToursBinding>(FragmentNearbyToursBinding::inflate) {

    private lateinit var nearByToursBinding: FragmentNearbyToursBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nearByToursBinding = FragmentNearbyToursBinding.inflate(inflater, container, false)
        return nearByToursBinding.root
    }
}