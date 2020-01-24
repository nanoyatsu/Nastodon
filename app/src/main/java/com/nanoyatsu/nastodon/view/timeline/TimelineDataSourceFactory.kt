package com.nanoyatsu.nastodon.view.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiTimelines
import com.nanoyatsu.nastodon.data.entity.Status

class TimelineDataSourceFactory(
    private val timelineKind: TimelineViewModel.Kind,
    private val apiDir: MastodonApiTimelines,
    private val token: String
) : DataSource.Factory<String, Status>() {
    val sourceLiveData = MutableLiveData<TimelineDataSource>()
    override fun create(): DataSource<String, Status> {
        val source = TimelineDataSource(timelineKind, apiDir, token)
        sourceLiveData.postValue(source)
        return source
    }
}