package com.zackyfaqia.ingstagram.data.di

import com.zackyfaqia.ingstagram.data.network.ApiConfig
import com.zackyfaqia.ingstagram.data.paging.StoryRepository

object Injection {
    fun provideRepository(token : String): StoryRepository {
        val apiService= ApiConfig.getApiService()
        return StoryRepository(apiService, token)
    }
}