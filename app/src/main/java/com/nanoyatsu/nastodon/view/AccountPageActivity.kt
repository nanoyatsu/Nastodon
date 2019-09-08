package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.activity_account_page.*
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import kotlin.reflect.KSuspendFunction2

class AccountPageActivity : AppCompatActivity() {
    enum class IntentKey { ACCOUNT }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_page)

        val account = intent.getParcelableExtra<Account>(IntentKey.ACCOUNT.name)

        Glide.with(this@AccountPageActivity).load(account.headerStatic).into(header)
        Glide.with(this@AccountPageActivity).load(account.avatarStatic).circleCrop().into(avatar)
        statusesCount.text = getString(R.string.accountStatusesCountFormat, account.statusesCount)
        displayName.text = account.displayName
        username.text = account.username
        note.text = Html.fromHtml(account.note, Html.FROM_HTML_MODE_COMPACT)


        val pref = AuthPreferenceManager(this@AccountPageActivity)
        val api = MastodonApiManager(pref.instanceUrl).api
        // todo 遷移してから読み込んで反映のほうがよさそう 渡し方を考える 部分適用みたいなことってできる？(_->Array<Account>にしたい)
        followingCount.also {
            it.text = getString(R.string.accountFollowingCountFormat, account.followersCount)
            it.setOnClickListener { transAccountPageActivity(account.id, api::getFollowingBy) }
        }

        followersCount.also {
            it.text = getString(R.string.accountFollowersCountFormat, account.followingCount)
            it.setOnClickListener { transAccountPageActivity(account.id, api::getFollowersBy) }
        }
    }

    private fun transAccountPageActivity(
        targetId: String,
        searchMethod: KSuspendFunction2<String, Int?, Response<Array<Account>>>
    ) {
        runBlocking {
            try {
                val res = searchMethod(targetId, null)
                val intent = Intent(this@AccountPageActivity, AccountsActivity::class.java).also {
                    it.putParcelableArrayListExtra(
                        AccountsActivity.IntentKey.LIST.name,
                        res.body()?.toCollection(ArrayList())
                    )
                }
                startActivity(intent)
            } catch (e: HttpException) {
                e.printStackTrace()
            }
        }
    }
}