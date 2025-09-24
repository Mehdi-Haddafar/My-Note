package com.example.notapplication.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notapplication.repository.ManagerData

class ViewModelFactoryForViewModelNotActivity ( val repository : ManagerData) :

    ViewModelProvider.Factory {

    override fun < T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelForNoteActivity::class.java)) {
            return ViewModelForNoteActivity (repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}