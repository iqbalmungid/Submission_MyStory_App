package com.iqbalmungid.mystory.data.local.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.iqbalmungid.mystory.data.remote.api.Api
import com.iqbalmungid.mystory.data.remote.response.Stories

class StorySource(private val apiService: Api, private val header: String) : PagingSource<Int, Stories>() {
    override fun getRefreshKey(state: PagingState<Int, Stories>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stories> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStories(header, page, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == INITIAL_PAGE_INDEX) null else page - 1,
                nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}