package com.zackyfaqia.ingstagram.ui.auth.login

import androidx.lifecycle.*
import com.google.gson.Gson
import com.zackyfaqia.ingstagram.data.model.UserModel
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.data.network.ApiConfig
import com.zackyfaqia.ingstagram.data.response.login.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val preference: UserPreference) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun getUser(): LiveData<UserModel> {
        return preference.getUser().asLiveData()
    }

    fun login(token: String) {
        viewModelScope.launch {
            preference.login(token)
        }
    }

    fun auth(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)

        client.enqueue(object : Callback<LoginResponse> {
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _message.value = t.message.toString()
            }

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _message.value = "Login Success"
                        viewModelScope.launch {
                            if (getUser().value == null) preference.saveUser(
                                UserModel(
                                    responseBody.loginResult.userId,
                                    responseBody.loginResult.name,
                                    true,
                                    responseBody.loginResult.token
                                )
                            )
                            preference.login(responseBody.loginResult.token)
                        }
                    } else {
                        _message.value = responseBody?.message
                    }
                } else {
                    val responseBody = Gson().fromJson(
                        response.errorBody()?.charStream(),
                        LoginResponse::class.java
                    )
                    _message.value = responseBody.message
                }
            }
        })
    }
}