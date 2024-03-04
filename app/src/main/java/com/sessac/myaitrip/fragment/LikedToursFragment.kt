package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentLikedToursBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 좋아요 목록 페이지
 */
class LikedToursFragment :
    ViewBindingBaseFragment<FragmentLikedToursBinding>(FragmentLikedToursBinding::inflate) {

    private lateinit var likedBinding: FragmentLikedToursBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        likedBinding = FragmentLikedToursBinding.inflate(inflater, container, false)
        return likedBinding.root
    }
}