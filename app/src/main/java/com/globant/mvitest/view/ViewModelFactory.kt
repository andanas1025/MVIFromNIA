package com.globant.mvitest.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.globant.mvitest.api.AnimalRepo

class ViewModelFactory(private val repo: AnimalRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}