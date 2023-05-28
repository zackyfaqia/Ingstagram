package com.zackyfaqia.ingstagram.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.zackyfaqia.ingstagram.data.paging.StoryRepository
import com.zackyfaqia.ingstagram.data.response.story.StoryItem

class ListPagingViewModel(storyRepository: StoryRepository) : ViewModel() {
    val list: LiveData<PagingData<StoryItem>> =
        storyRepository.getStory().cachedIn(viewModelScope)
}