package com.zackyfaqia.ingstagram.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zackyfaqia.ingstagram.data.model.UserPreference
import com.zackyfaqia.ingstagram.ui.add.AddStoryViewModel
import com.zackyfaqia.ingstagram.ui.auth.login.LoginViewModel
import com.zackyfaqia.ingstagram.ui.auth.register.RegisterViewModel
import com.zackyfaqia.ingstagram.ui.main.MainViewModel

class ViewModelFactory(private val preference: UserPreference) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preference) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preference) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(preference) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}