package com.sessac.myaitrip.presentation.tours.source

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sessac.myaitrip.data.database.TourDao
import com.sessac.myaitrip.data.entities.TourItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TourPagingSource(
    private val tourDao: TourDao,
    private val area: String,
    private val category: String,
    private val inputText: String
) : PagingSource<Int, TourItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TourItem> {
        return try {

            val pageSize = params.loadSize
            val offset = params.key ?: 0

            /**
             * 관광지 리스트 조회
             * 1. 지역과 카테고리로 관광지 리스트 조회
             * 2. 지역, 카테고리, 입력한 키워드로 관광지 리스트 조회
             */
            val tourList = withContext(Dispatchers.IO) {
                if (inputText == "null")
                    tourDao.getTourList(area, category, pageSize, offset)
                else
                    tourDao.getTourList(area, category, inputText, pageSize, offset)
            }

            LoadResult.Page(
                data = tourList,
                prevKey = if (offset == 0) null else offset - pageSize,
                nextKey = if (tourList.size < pageSize) null else offset + pageSize
            )
        } catch (e: Exception) {
            Log.d("TourPagingSource", "TourPagingSource error : $e")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TourItem>): Int? {
        TODO("Not yet implemented")
    }
}