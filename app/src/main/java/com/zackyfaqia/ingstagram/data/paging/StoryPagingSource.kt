package com.zackyfaqia.ingstagram.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.zackyfaqia.ingstagram.data.Constants.INITIAL_PAGE_INDEX
import com.zackyfaqia.ingstagram.data.network.ApiService
import com.zackyfaqia.ingstagram.data.response.story.StoryItem

class StoryPagingSource(private val apiService: ApiService, private val token: String) :
    PagingSource<Int, StoryItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories("Bearer ${token}", position, params.loadSize)
            val dataList = responseData.listStory

            LoadResult.Page(
                data = dataList,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (dataList.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, StoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}