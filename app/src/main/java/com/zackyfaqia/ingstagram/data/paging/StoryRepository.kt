package com.zackyfaqia.ingstagram.data.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.zackyfaqia.ingstagram.data.network.ApiService
import com.zackyfaqia.ingstagram.data.response.story.StoryItem

class StoryRepository(private val apiService: ApiService, private val token: String) {
    fun getStory(): LiveData<PagingData<StoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }
}