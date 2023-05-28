package com.zackyfaqia.ingstagram.data.model

data class UserModel(
    val id: String,
    val name: String,
    val isLogin: Boolean,
    val token: String = ""
)
