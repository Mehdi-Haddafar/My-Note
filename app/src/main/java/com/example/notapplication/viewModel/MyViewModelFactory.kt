package com.example.notapplication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notapplication.repository.ManagerData


class MyViewModelFactory( val repository : ManagerData) :

    ViewModelProvider.Factory {

    override fun <T : ViewModel> create (modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelForListActivity::class.java)) {
            return ViewModelForListActivity(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
