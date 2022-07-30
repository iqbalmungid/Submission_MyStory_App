package com.iqbalmungid.mystory.ui.poststory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.iqbalmungid.mystory.data.local.datastore.Account
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.data.remote.api.Client
import com.iqbalmungid.mystory.data.remote.response.ApiResponse
import com.iqbalmungid.mystory.helper.ApiCallbackString
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostViewModel(private val pref: AccountPreferences): ViewModel() {

    fun postStories(
        user: Account,
        desc: RequestBody,
        img: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null,
        param: ApiCallbackString
    ) {
        Client.apiInstance
            .postStories("Bearer ${user.token}", desc, img, lat, lon)
            .enqueue(object : Callback<ApiResponse>{
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            param.onResponse(response.body() != null, SUCCESS)
                        } else {
                            Log.e(TAG, "onFailure: ${response.message()}")
                            val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                            val message = jsonObject.getString("message")
                            param.onResponse(false, message)
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")
                    param.onResponse(false, t.message.toString())
                }
            })
    }

    fun getUser(): LiveData<Account> {
        return pref.getUser().asLiveData()
    }

    fun signout() {
        viewModelScope.launch {
            pref.signout()
        }
    }

    fun getLocationSettings(): LiveData<Boolean> {
        return pref.getLocationSetting().asLiveData()
    }

    fun saveLocationSetting(isLocationActive: Boolean) {
        viewModelScope.launch {
            pref.saveLocationSetting(isLocationActive)
        }
    }

    companion object {
        private const val TAG = "PostStoryViewModel"
        private const val SUCCESS = "success"
    }
}