package com.sessac.myaitrip.presentation.myPage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.sessac.myaitrip.R
import com.sessac.myaitrip.common.TOUR_CONTENT_ID
import com.sessac.myaitrip.data.entities.DiaryItem
import com.sessac.myaitrip.data.entities.TourItem
import com.sessac.myaitrip.databinding.FragmentMyPageBinding
import com.sessac.myaitrip.presentation.common.UiState
import com.sessac.myaitrip.presentation.common.ViewBindingBaseFragment
import com.sessac.myaitrip.presentation.common.ViewModelFactory
import com.sessac.myaitrip.presentation.home.HomeViewModel
import com.sessac.myaitrip.presentation.home.adapter.SmallCardAdapter
import com.sessac.myaitrip.presentation.myPage.adapter.DiaryAdapter
import com.sessac.myaitrip.util.GlideUtil
import com.sessac.myaitrip.util.repeatOnCreated
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 내 정보 페이지
 */
class MyPageFragment :
    ViewBindingBaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::inflate) {

    private val myPageViewModel: MyPageViewModel by viewModels { ViewModelFactory() }

    private lateinit var likeRecyclerView: RecyclerView
    private lateinit var likeAdapter: SmallCardAdapter
    private lateinit var diaryRecyclerView: RecyclerView
    private lateinit var diaryAdapter: DiaryAdapter
    private lateinit var userId: String
    private lateinit var tourLikeList: MutableList<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
    }

    private fun init() {
        getUser()
        setupUserCollect()
    }

    /**
     * 유저 조회
     */
    private fun getUser() {
        myPageViewModel.getUserPreferences()
    }

    /**
     * 유저 정보 조회
     */
    private fun getUserProfile() {
        myPageViewModel.getUserProfileFromFireBase(userId)
    }

    /**
     * 유저 좋아요 조회
     */
    private fun getUserLikeList() {
        myPageViewModel.getUserLikeListFromFireBase(userId)
    }

    /**
     * 유저 정보 조회
     */
    private fun getDiary() {
        myPageViewModel.getDiaryFromFireBase(userId)
    }

    /**
     * 좋아요한 관광지 정보 조회
     */
    private fun getLikeTourList() {
        myPageViewModel.getLikeTourList(tourLikeList)
    }

    private fun setupUserCollect() {
        /**
         * 유저 정보 조회
         */
        repeatOnCreated {
            myPageViewModel.userPreferenceStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        userId = state.data.userId
                        getUserProfile()
                        getUserLikeList()
                        setUpUserProfileCollect()
                        setupLikeCollect()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setUpUserProfileCollect() {

        /**
         * FireBase 유저 정보 조회
         */
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.userProfileStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        val userProfile = state.data

                        with(binding) {
                            GlideUtil.loadImage(
                                ivUserImg.context,
                                userProfile["profileImgUrl"] as String,
                                ivUserImg
                            )
                            tvUserName.text = userProfile["nickname"] as String
                        }
                    }

                    is UiState.Error -> {
                        myPageViewModel.getUserLikeListFromFireBase(userId)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setupLikeCollect() {
        /**
         * FireBase 유저 좋아요 관광지 리스트 조회
         */
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.userLikeStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        tourLikeList = state.data
                        setupRecyclerviewAdapter()
                    }

                    is UiState.Error -> {
                        myPageViewModel.getUserLikeListFromFireBase(userId)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun setUpDiaryCollect() {
        /**
         * 다이어리 리스트 조회
         *
         */
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.diaryStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        with(binding) {
                            val isEmpty = state.data.isEmpty()
                            tvDiaryListEmpty.visibility =
                                if (isEmpty) View.VISIBLE else View.INVISIBLE
                            rcvDiary.visibility = if (isEmpty) View.INVISIBLE else View.VISIBLE

                            if (!isEmpty) {
                                diaryAdapter.setDiaryList(state.data)
                                diaryRecyclerView.scrollToPosition(0)
                            }
                        }
                    }

                    is UiState.Loading -> {

                    }

                    is UiState.Error -> {
                        Log.e("TourAPI HandleState", "${state.errorMessage}")
                    }

                    else -> {
                        Log.e("TourAPI HandleState", "$state")

                        if (state.toString() == "Empty") {
                            with(binding) {
                                tvDiaryListEmpty.visibility = View.VISIBLE
                                rcvDiary.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Room에서 조회한 관광지 리스트 adapter submit
     *
     * 1. popular   : 인기 관광지 리스트
     * 2. recommend : 추천 관광지 리스트
     * 3. nearby    : 내 주변 추천 관광지 리스트
     */
    private fun setupTourListCollect() {
        viewLifecycleOwner.lifecycleScope.launch {
            myPageViewModel.likeTourListStatus.collectLatest { state ->
                when (state) {
                    is UiState.Success -> {
                        Log.d("test", "test data : ${state.data}")
                        with(binding) {
                            val isEmpty = state.data.isEmpty()
                            tvLikeListEmpty.visibility =
                                if (isEmpty) View.VISIBLE else View.INVISIBLE
                            rcvLike.visibility = if (isEmpty) View.INVISIBLE else View.VISIBLE

                            if (!isEmpty) {
                                likeAdapter.setTourList(state.data)
                                likeRecyclerView.scrollToPosition(0)
                            }
                        }
                    }

                    is UiState.Error -> {
                        Log.e(
                            "TourAPI HandleState",
                            "popularTourList error : ${state.errorMessage}"
                        )
                    }

                    is UiState.Loading -> {
                        Log.e("TourAPI HandleState", "popularTourList loading : $$state")
                    }

                    else -> {
                        Log.e("TourAPI HandleState", "popularTourList else : $state")

                        if (state.toString() == "Empty") {
                            with(binding) {
                                tvLikeListEmpty.visibility = View.VISIBLE
                                rcvLike.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 각 리사이클러 뷰 설정
     */
    private fun setupRecyclerviewAdapter() {
        likeRecyclerView = binding.rcvLike
        diaryRecyclerView = binding.rcvDiary

        /**
         * 관광지 클릭 시
         */
        val likeItemOnClick: (TourItem) -> Unit = { tourItem ->
            val bundle = Bundle().apply {
                putString(TOUR_CONTENT_ID, tourItem.contentId)
            }
            findNavController().navigate(R.id.action_MyPageFragment_to_TourDetailFragment, bundle)
        }

        val diaryItemOnClick: (DiaryItem) -> Unit = { diaryItem ->
            val bundle = Bundle().apply {
                putString(TOUR_CONTENT_ID, diaryItem.contentId)
            }
            findNavController().navigate(R.id.action_MyPageFragment_to_TourDetailFragment, bundle)
        }

        /**
         * 관광지 좋아요 클릭 시
         */
        val likeOnClick: (AppCompatImageView, String) -> Unit = { _, contentId ->
            myPageViewModel.updateUserLikeListFromFireBase(userId, contentId)
            getUserLikeList()
        }

        /**
         * 관광지 좋아요 상태
         */
        val liked: (AppCompatImageView) -> Unit = { likeImage ->
            likeImage.tag = "true"
            likeImage.setImageResource(R.drawable.ic_like_selected)
        }

        /**
         * 1. likeItemOnClick : 좋아요 아이템 클릭 시
         * 2. diaryItemOnClick : 다이어리 아이템 클릭 시
         * 3. likeOnClick : 좋아요 버튼 클릭 시
         * 4. liked       : 관광지가 좋아요 상태일 때
         * 5. tourLikeList : FireBase 좋아요 리스트
         */

        likeAdapter = SmallCardAdapter(
            likeItemOnClick, likeOnClick, liked, tourLikeList,
            viewLifecycleOwner.lifecycleScope
        )

        diaryAdapter = DiaryAdapter(diaryItemOnClick, viewLifecycleOwner.lifecycleScope)

        likeRecyclerView.adapter = likeAdapter
        diaryRecyclerView.adapter = diaryAdapter

        getLikeTourList()
        setupTourListCollect()
        getDiary()
        setUpDiaryCollect()
    }
}