package com.sessac.myaitrip.presentation.progress

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.sessac.myaitrip.MainActivity
import com.sessac.myaitrip.common.DEFAULT_NUM_OF_ROWS
import com.sessac.myaitrip.common.DEFAULT_PAGE_NUMBER
import com.sessac.myaitrip.common.DEFAULT_TOTAL_COUNT
import com.sessac.myaitrip.databinding.ActivityProgressBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseActivity
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProgressActivity :
    ViewBindingBaseActivity<ActivityProgressBinding>({ ActivityProgressBinding.inflate(it) }) {

    private val progressViewModel: ProgressViewModel by viewModels() { ViewModelFactory() }
    private lateinit var progressBar: ProgressBar

    private var prefTotalCount = DEFAULT_TOTAL_COUNT
    private var prefPageNumber = DEFAULT_PAGE_NUMBER
    private val numOfRows = DEFAULT_NUM_OF_ROWS
    private var errorCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = binding.progressBar

        setUpCollect()
    }

    private fun setUpCollect() {
        lifecycleScope.launch {
            progressViewModel.tourPreferenceStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        prefTotalCount = state.data.totalCount
                        prefPageNumber = state.data.pageNumber
                        getFirstTourItemForTotalCount()
                    }

                    is UiState.Error -> {
                        errorCount++
                        if (errorCount >= 3) {
                            Log.e("TourAPI HandleState", "API 연결이 5번 실패했습니다.")
                            moveToMain()
                        } else {
                            delay(2000)
                            setUpCollect()
                        }
                    }

                    else -> {}
                }
            }
        }

        /**
         * 관광지 1개 아이템 호출
         *
         * sharedPref
         * 1. totalCount : 관광지 api 총 아이템 개수 저장. 아이템 개수가 다를 때 데이터 다시 조회 및 저장
         * 2. prefPageNumber : 조회된 pageNumber를 저장하여, 만약 강제 종료 후 재실행 시 마지막 저장한 곳 부터 저장함.
         *
         */

        /**
         * 관광지 전체 리스트 호출
         *
         * 처음 설치 시, 아이템이 늘어난 경우, 마지막으로 저장한 pageNumber가 마지막이 아닌 경우 전체 리스트 조회 및 저장
         *
         */
        lifecycleScope.launch {
            progressViewModel.tourApiStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val response = state.data.response.body

                        val totalCount: Int = response.totalCount ?: 0
                        val lastPageNumber =
                            (totalCount + (DEFAULT_NUM_OF_ROWS - DEFAULT_PAGE_NUMBER)) / DEFAULT_NUM_OF_ROWS

                        if (checkTourItemFromApi(totalCount, lastPageNumber)) {
                            ++prefPageNumber
                            prefTotalCount = totalCount
                            getTourItemFromApi()
                        } else {
                            progressViewModel.updateTourPreference(prefTotalCount, prefPageNumber)
                            moveToMain()
                        }

                        response.items?.item?.takeIf { response.numOfRows == numOfRows }?.let {
                            progressViewModel.insertTour(it)
                            updateProgress(prefPageNumber, lastPageNumber)
                        }
                    }

                    is UiState.Error -> {
                        Log.e("TourAPI HandleState", "${state.errorMessage}")
                        errorCount++
                        if (errorCount >= 3) {
                            Log.e("TourAPI HandleState", "API 연결이 5번 실패했습니다.")
                            moveToMain()
                        } else {
                            delay(2000)
                            setUpCollect()
                        }
                    }

                    else -> {
                        Log.e("TourAPI HandleState", "$state")
                    }
                }
            }
        }
    }

    private fun getFirstTourItemForTotalCount() {
        lifecycleScope.launch {
            progressViewModel.getTourFromAPI(prefPageNumber, 1)
        }
    }

    private fun getTourItemFromApi() {
        lifecycleScope.launch {
            progressViewModel.getTourFromAPI(prefPageNumber, numOfRows)
        }
    }

    private fun checkTourItemFromApi(totalCount: Int, lastPageNumber: Int) =
        prefTotalCount == -1 || totalCount > prefTotalCount || lastPageNumber != prefPageNumber


    private fun moveToMain() {
        Intent(this@ProgressActivity, MainActivity::class.java).also {
            it.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
        }
    }

    private fun updateProgress(progress: Int, max: Int) {
        val percentage = (progress.toFloat() / max.toFloat() * 100).toInt()
        with(progressBar) {
            this.max = max
            this.progress = progress
        }

        with(binding) {
            progressPercent.text = "$percentage%"
            progressCounting.text = "$progress / $max"
        }
    }
}