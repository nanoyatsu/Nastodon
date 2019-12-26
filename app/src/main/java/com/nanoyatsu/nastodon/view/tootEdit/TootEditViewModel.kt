package com.nanoyatsu.nastodon.view.tootEdit

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

class TootEditViewModel(
    private val auth: AuthInfo,
    private val apiManager: MastodonApiManager
) : ViewModel() {}