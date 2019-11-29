package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.nanoyatsu.nastodon.model.Status

class CardTootViewModel(initToot: Status) {
    private var _toot = MutableLiveData<Status>()
    val toot: LiveData<Status>
        get() = _toot
    private val _reblogEvent = MutableLiveData<Boolean>()
    val reblogEvent: LiveData<Boolean>
        get() = _reblogEvent
    private val _favouriteEvent = MutableLiveData<Boolean>()
    val favouriteEvent: LiveData<Boolean>
        get() = _favouriteEvent

    init {
        _toot.value = initToot
        _reblogEvent.value = false
        _favouriteEvent.value = false
    }

    // Toot:Statusのうち、変動する値
    val repliesCount = Transformations.map(toot) { it.repliesCount }
    val reblogsCount = Transformations.map(toot) { it.reblogsCount }
    val favouritesCount = Transformations.map(toot) { it.favouritesCount }
    val reblogged = Transformations.map(toot) { it.reblogged }
    val favourited = Transformations.map(toot) { it.favourited }

    fun onReblogClicked() = run { _reblogEvent.value = true }
    fun onReblogFinished() = run { _reblogEvent.value = false }
    fun onFavouriteClicked() = run { _favouriteEvent.value = true }
    fun onFavouriteFinished() = run { _favouriteEvent.value = false }
}