package com.sessac.myaitrip.presentation.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sessac.myaitrip.databinding.FragmentDiaryBinding
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.home.HomeViewModel

/**
 * 다이어리 페이지
 */
class DiaryFragment :
    ViewBindingBaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::inflate) {

    private val diaryViewModel: DiaryViewModel by viewModels() { ViewModelFactory(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}