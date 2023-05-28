package com.zackyfaqia.ingstagram.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zackyfaqia.ingstagram.data.di.Injection
import com.zackyfaqia.ingstagram.ui.main.ListPagingViewModel

class ViewModelFactoryPaging(private val token : String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListPagingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListPagingViewModel(Injection.provideRepository(token)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}