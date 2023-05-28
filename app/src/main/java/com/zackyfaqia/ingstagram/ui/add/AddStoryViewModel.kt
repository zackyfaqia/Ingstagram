package com.zackyfaqia.ingstagram.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.zackyfaqia.ingstagram.data.network.ApiConfig
import com.zackyfaqia.ingstagram.data.response.login.LoginResponse
import com.zackyfaqia.ingstagram.data.response.story.StoryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddStoryViewModel : ViewModel() {
    private var _isUpload = MutableLiveData<Boolean>()
    val isUpload: LiveData<Boolean> = _isUpload

    private var _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private var _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun uploadToServer(
        token: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ) {
        _isUpload.value = true
        val client =
            ApiConfig.getApiService().uploadStory("Bearer $token", imageMultipart, description)

        client.enqueue(object : Callback<StoryUploadResponse> {
            override fun onResponse(
                call: Call<StoryUploadResponse>,
                response: Response<StoryUploadResponse>
            ) {
                _isUpload.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if ((responseBody != null) && (responseBody.error == false)) {
                        _message.value = "success"
                        _isError.value = false
                    }
                } else {
                    val responseBody = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        LoginResponse::class.java
                    )
                    _message.value = responseBody.message
                }
            }

            override fun onFailure(call: Call<StoryUploadResponse>, t: Throwable) {
                _isUpload.value = false
                _message.value = "Failed"
            }
        })
    }
}