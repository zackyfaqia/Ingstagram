package com.zackyfaqia.ingstagram.data.response.story

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @field:SerializedName("listStory")
    val listStory: List<StoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
