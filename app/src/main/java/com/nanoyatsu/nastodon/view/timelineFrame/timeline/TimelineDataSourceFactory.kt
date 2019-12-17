package com.nanoyatsu.nastodon.view.timelineFrame.timeline

import androidx.paging.DataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.api.entity.Status

class TimelineDataSourceFactory(
    private val timelineKind: TimelineViewModel.Kind,
    private val apiDir: MastodonApiTimelines,
    private val token: String
) : DataSource.Factory<String, Status>() {
    override fun create(): DataSource<String, Status> {
        return TimelineDataSource(timelineKind, apiDir, token)
    }
}