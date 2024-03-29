package com.sessac.myaitrip.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentUserBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 내 정보 페이지
 */
class UserFragment :
    ViewBindingBaseFragment<FragmentUserBinding>(FragmentUserBinding::inflate) {

    private lateinit var userBinding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userBinding = FragmentUserBinding.inflate(inflater, container, false)
        return userBinding.root
    }
}