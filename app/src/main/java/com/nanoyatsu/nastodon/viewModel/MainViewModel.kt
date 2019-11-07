package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.R

data class MainViewModel(
    var selectedTabId: Int = R.id.navigation_timeline
) : ViewModel()