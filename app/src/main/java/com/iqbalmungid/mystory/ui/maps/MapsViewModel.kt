package com.iqbalmungid.mystory.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.iqbalmungid.mystory.data.local.datastore.Account
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.data.remote.api.Client
import com.iqbalmungid.mystory.data.remote.response.Stories
import com.iqbalmungid.mystory.data.remote.response.StoriesResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: AccountPreferences): ViewModel() {
    val listStories = MutableLiveData<ArrayList<Stories>>()

    fun setStories(token: String) {
        Client.apiInstance
            .getStoriesLocation("Bearer $token")
            .enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        listStories.postValue(response.body()?.listStory as ArrayList<Stories>?)
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    Log.d("Failure", t.message.toString())
                }
            })
    }

    fun getStories(): LiveData<ArrayList<Stories>> {
        return listStories
    }

    fun getUser(): LiveData<Account> {
        return pref.getUser().asLiveData()
    }
}