package com.iqbalmungid.mystory.di

import android.content.Context
import com.iqbalmungid.mystory.data.local.paging.StoryRepository
import com.iqbalmungid.mystory.data.local.room.StoryDatabase
import com.iqbalmungid.mystory.data.remote.api.Client

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = Client.apiInstance
        return StoryRepository(database, apiService)
    }
}