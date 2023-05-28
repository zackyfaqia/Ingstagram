package com.zackyfaqia.ingstagram.data.response.login

import com.google.gson.annotations.SerializedName

data class LoginResult(
    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("userId")
    val userId: String,

    @field:SerializedName("token")
    val token: String
)
