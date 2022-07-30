package com.iqbalmungid.mystory.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import com.iqbalmungid.mystory.data.remote.api.Client
import com.iqbalmungid.mystory.data.remote.response.ApiResponse
import com.iqbalmungid.mystory.helper.ApiCallbackString
import org.json.JSONObject
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    fun signup(name: String, email: String, pass: String, param: ApiCallbackString){
        Client.apiInstance
            .register(name, email, pass)
            .enqueue(object : Callback<ApiResponse>{
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful){
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error)
                            param.onResponse(response.body() != null, SUCCESS)
                    } else {
                        Log.e(TAG, "onFailure1: ${response.message()}")
                        val jsonObject = JSONTokener(response.errorBody()!!.string()).nextValue() as JSONObject
                        val message = jsonObject.getString("message")
                        param.onResponse(false, message)
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure2: ${t.message}")
                    param.onResponse(false, t.message.toString())
                }
            })
    }
    companion object {
        const val TAG = "RegisterViewModel"
        const val SUCCESS = "success"
    }
}