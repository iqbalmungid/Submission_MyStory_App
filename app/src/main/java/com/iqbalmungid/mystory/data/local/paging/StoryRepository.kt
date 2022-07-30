package com.iqbalmungid.mystory.data.local.paging

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.iqbalmungid.mystory.data.local.room.StoryDatabase
import com.iqbalmungid.mystory.data.remote.api.Api
import com.iqbalmungid.mystory.data.remote.response.Stories

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: Api) {
    fun getStoriesPaging(header: String): LiveData<PagingData<Stories>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, header),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }
}