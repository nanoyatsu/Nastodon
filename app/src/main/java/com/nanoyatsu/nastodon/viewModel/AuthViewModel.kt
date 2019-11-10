package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.entity.AuthInfo

data class AuthViewModel(
    var authInfo: AuthInfo = AuthInfo()
) : ViewModel()