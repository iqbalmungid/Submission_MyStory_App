package com.iqbalmungid.mystory.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.iqbalmungid.mystory.data.remote.response.Stories

@Dao
interface StoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(quote: List<Stories>)

    @Query("SELECT * FROM stories")
    fun getAllStory(): PagingSource<Int, Stories>

    @Query("DELETE FROM stories")
    suspend fun deleteAll()
}