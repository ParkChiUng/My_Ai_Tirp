package com.sessac.myaitrip.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentUserDetailBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 프로필 상세 페이지
 */
class UserDetailFragment :
    ViewBindingBaseFragment<FragmentUserDetailBinding>(FragmentUserDetailBinding::inflate) {

    private lateinit var userDetailBinding: FragmentUserDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userDetailBinding = FragmentUserDetailBinding.inflate(inflater, container, false)
        return userDetailBinding.root
    }
}