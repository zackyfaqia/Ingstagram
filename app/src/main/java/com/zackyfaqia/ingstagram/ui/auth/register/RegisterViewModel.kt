package com.zackyfaqia.ingstagram.ui.auth.register

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.zackyfaqia.ingstagram.data.model.UserModel
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.data.network.ApiConfig
import com.zackyfaqia.ingstagram.data.response.register.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val preference: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> = _isError

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().register(name, email, password)

        client.enqueue(object : Callback<RegisterResponse> {
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message.toString()
            }

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d(TAG, "onResponse: $responseBody")
                    if (responseBody != null) {
                        viewModelScope.launch {
                            preference.saveUser(UserModel("", name, false, ""))
                        }
                        _isError.value = false

                    } else {
                        _isError.value = true
                    }
                } else {
                    val responseBody = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        RegisterResponse::class.java
                    )
                    _message.value = responseBody.message.toString()
                    _isError.value = true
                }
            }
        })
    }
}