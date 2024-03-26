//package com.sessac.myaitrip.presentation.airecommend
//
//import android.util.Log
//import androidx.paging.PagingSource
//import androidx.paging.PagingState
//import com.sessac.myaitrip.data.database.TourDao
//import com.sessac.myaitrip.data.entities.TourItem
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.withContext
//
//class AIRecommendPagingSource(
//    private val tourDao: TourDao,
//    private val area: String,
//    private val inputText: String
//) : PagingSource<Int, TourItem>() {
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourItem> {
//        return try {
//
//            val pageSize = params.loadSize
//            val offset = params.key ?: 0
//
//            val tourList = withContext(Dispatchers.IO) {
//                tourDao.getRecommendTourList(inputText, area, pageSize, offset)
//            }
//
//            LoadResult.Page(
//                data = tourList,
//                prevKey = if (offset == 0) null else offset - pageSize,
//                nextKey = if (tourList.size < pageSize) null else offset + pageSize
//            )
//        } catch (e: Exception) {
//            Log.d("AIRecommendPagingSource", "PagingSource error : $e")
//            LoadResult.Error(e)
//        }
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, TourItem>): Int? {
//        TODO("Not yet implemented")
//    }
//}