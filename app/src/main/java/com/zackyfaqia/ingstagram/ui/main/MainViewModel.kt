package com.zackyfaqia.ingstagram.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.zackyfaqia.ingstagram.data.model.UserModel
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.data.network.ApiConfig
import com.zackyfaqia.ingstagram.data.response.story.StoryItem
import com.zackyfaqia.ingstagram.data.response.story.StoryResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val preference: UserPreference) : ViewModel() {

    private val _listStories = MutableLiveData<List<StoryItem>>()
    val listStories: LiveData<List<StoryItem>> = _listStories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _locationStory = MutableLiveData<List<StoryItem>>()
    val locationStory: LiveData<List<StoryItem>> = _locationStory

    private var _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> = _errorMsg

    fun getUser(): LiveData<UserModel> {
        return preference.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            preference.logout()
        }
    }

    fun getStoryLocation(token: String) {
        val client = ApiConfig.getApiService().getStoryLocation("Bearer $token")
        client.enqueue(object : Callback<StoryResponse> {

            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _locationStory.value = responseBody.listStory
                        _errorMsg.value = responseBody.listStory.toString()
                    }
                    _errorMsg.value = responseBody?.message.toString()
                }
            }
            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _errorMsg.value = t.message.toString()
            }
        })
    }

}