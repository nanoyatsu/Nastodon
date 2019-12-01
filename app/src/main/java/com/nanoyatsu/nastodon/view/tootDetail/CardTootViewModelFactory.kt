package com.nanoyatsu.nastodon.view.tootDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

class CardTootViewModelFactory(
    private val initToot: Status,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardTootViewModel::class.java)) {
            return CardTootViewModel(initToot, auth, apiManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
