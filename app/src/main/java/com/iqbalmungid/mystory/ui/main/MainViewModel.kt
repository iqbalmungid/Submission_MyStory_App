package com.iqbalmungid.mystory.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.iqbalmungid.mystory.data.local.paging.StoryRepository
import com.iqbalmungid.mystory.data.remote.response.Stories

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStories(header: String): LiveData<PagingData<Stories>> =
        storyRepository.getStoriesPaging(header).cachedIn(viewModelScope)
}