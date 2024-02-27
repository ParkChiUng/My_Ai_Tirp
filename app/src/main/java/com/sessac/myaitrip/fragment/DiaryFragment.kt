package com.sessac.myaitrip.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sessac.myaitrip.databinding.FragmentDiaryBinding

/**
 * 다이어리 페이지
 */
class DiaryFragment :
    ViewBindingBaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::inflate) {

    private lateinit var diaryBinding: FragmentDiaryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        diaryBinding = FragmentDiaryBinding.inflate(inflater, container, false)
        return diaryBinding.root
    }
}