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
import com.nanoyatsu.nastodon.databinding.FragmentTootDetailBinding
import javax.inject.Inject

class TootDetailActivity : AppCompatActivity() {
    enum class IntentKey { TOOT }

    lateinit var binding: FragmentTootDetailBinding
    @Inject
    lateinit var auth: AuthInfo
    @Inject
    lateinit var apiManager: MastodonApiManager


    override fun onCreate(savedInstanceState: Bundle?) {
        (application as NastodonApplication).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        val toot = intent.getParcelableExtra<Status>(IntentKey.TOOT.name)

        binding =
            DataBindingUtil.setContentView<FragmentTootDetailBinding>(
                this,
                R.layout.fragment_toot_detail
            )
                .also {
                    val factory = TootViewModelFactory(toot, auth, apiManager)
                    it.vm = ViewModelProvider(this@TootDetailActivity, factory)
                        .get(TootViewModel::class.java)
                    it.lifecycleOwner = this@TootDetailActivity
                }
    }
}
