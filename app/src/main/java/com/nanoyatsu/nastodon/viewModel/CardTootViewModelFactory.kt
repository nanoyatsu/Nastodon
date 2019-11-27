package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.model.Status

class CardTootViewModelFactory(private val toot: Status) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardTootViewModel::class.java))
            return CardTootViewModel(toot) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}