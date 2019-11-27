package com.nanoyatsu.nastodon.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.model.Status

class CardTootViewModel(initToot: Status) : ViewModel() {
    private var _toot = MutableLiveData<Status>()
    val toot: LiveData<Status>
        get() = _toot

    init {
        _toot.value = initToot
    }

    // Toot:Statusのうち、変動する値
    val repliesCount = Transformations.map(toot) { it.repliesCount }
    val reblogsCount = Transformations.map(toot) { it.reblogsCount }
    val favouritesCount = Transformations.map(toot) { it.favouritesCount }
    val reblogged = Transformations.map(toot) { it.reblogged }
    val favourited = Transformations.map(toot) { it.favourited }
}