package com.zackyfaqia.ingstagram.data.network

import com.zackyfaqia.ingstagram.data.response.login.LoginResponse
import com.zackyfaqia.ingstagram.data.response.register.RegisterResponse
import com.zackyfaqia.ingstagram.data.response.story.StoryResponse
import com.zackyfaqia.ingstagram.data.response.story.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

// Interface for defining REST request functions
interface ApiService {
    // login
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    // register
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    // get stories
    @GET("stories")
    fun getStories(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): StoryResponse

    // post story
    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") authorization: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<StoryUploadResponse>

    // maps
    @GET("stories?location=1")
    fun getStoryLocation(
        @Header("Authorization") authorization: String
    ): Call<StoryResponse>
}