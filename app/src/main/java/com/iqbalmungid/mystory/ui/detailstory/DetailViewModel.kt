package com.iqbalmungid.mystory.ui.detailstory

import androidx.lifecycle.ViewModel
import com.iqbalmungid.mystory.data.remote.response.Stories

class DetailViewModel: ViewModel() {
    lateinit var listStory: Stories

    fun setDetail(stories: Stories): Stories {
        listStory = stories
        return listStory
    }
}