package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentSearchBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment

/**
 * 통합 검색 페이지
 */
class SearchFragment :
    ViewBindingBaseFragment<FragmentSearchBinding>(FragmentSearchBinding::inflate) {

    private lateinit var searchBinding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return searchBinding.root
    }
}