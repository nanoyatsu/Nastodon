package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.lifecycle.ViewModel

class TimelineViewModel(val getMethod: GetMethod) : ViewModel() {
    enum class GetMethod { HOME, LOCAL, GLOBAL }


}
