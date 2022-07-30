package com.iqbalmungid.mystory.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    @field:SerializedName("listStory")
    val listStory: List<Stories>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
