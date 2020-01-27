package com.nanoyatsu.nastodon.view.tootEdit


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.domain.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

class TootEditViewModelFactory(
    private val replyTo: Status?,
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) :
    ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TootEditViewModel::class.java)) {
            return TootEditViewModel(replyTo, auth, apiManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
