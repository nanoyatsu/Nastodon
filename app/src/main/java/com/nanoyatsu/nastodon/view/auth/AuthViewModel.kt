package com.nanoyatsu.nastodon.view.auth

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo

data class AuthViewModel(
    var authInfo: AuthInfo = AuthInfo()
) : ViewModel()