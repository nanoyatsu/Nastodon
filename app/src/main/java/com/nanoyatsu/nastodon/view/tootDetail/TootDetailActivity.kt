package com.nanoyatsu.nastodon.view.tootDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.nanoyatsu.nastodon.NastodonApplication
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.Status
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import com.nanoyatsu.nastodon.databinding.ActivityTootDetailBinding
import javax.inject.Inject

class TootDetailActivity : AppCompatActivity() {
    enum class IntentKey { TOOT }

    lateinit var binding: ActivityTootDetailBinding
    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NastodonApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        val toot = intent.getParcelableExtra<Status>(IntentKey.TOOT.name)

        binding =
            DataBindingUtil.setContentView<ActivityTootDetailBinding>(
                this,
                R.layout.activity_toot_detail
            )
                .also {
                    val factory = CardTootViewModelFactory(toot, auth, apiManager)
                    it.vm = ViewModelProvider(this@TootDetailActivity, factory)
                        .get(CardTootViewModel::class.java)
                    it.lifecycleOwner = this@TootDetailActivity
                }
    }
}
