package com.iqbalmungid.mystory.data.remote.api

import com.iqbalmungid.mystory.data.remote.response.ApiResponse
import com.iqbalmungid.mystory.data.remote.response.LoginResponse
import com.iqbalmungid.mystory.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<ApiResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") pass: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoriesResponse

    @Multipart
    @POST("stories")
    fun postStories(
        @Header("Authorization") token: String,
        @Part ("description") desc: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") latitude: RequestBody?,
        @Part("lon") longitude: RequestBody?
    ): Call<ApiResponse>

    @GET("stories?location=1")
    fun getStoriesLocation(
        @Header("Authorization") token: String
    ): Call<StoriesResponse>
}