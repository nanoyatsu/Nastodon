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
import kotlin.reflect.KSuspendFunction3

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
        //          →Parcelable乗せたクラス用意してその中に取得関数を持つくらいが妥当そう 持つのはDeferred(async)にしたらカッコつくかも（しれない）
        // fixme トークン必要があとから判明したりして書き方がひどめ
        followingCount.also {
            it.text = getString(R.string.accountFollowingCountFormat, account.followingCount)
            it.setOnClickListener { transAccountPageActivity(pref, account.id, api::getFollowingById) }
        }

        followersCount.also {
            it.text = getString(R.string.accountFollowersCountFormat, account.followersCount)
            it.setOnClickListener { transAccountPageActivity(pref, account.id, api::getFollowersById) }
        }
    }

    private fun transAccountPageActivity(
        pref: AuthPreferenceManager,
        targetId: String,
        searchMethod: KSuspendFunction3<String, String, Int?, Response<Array<Account>>>
    ) {
        runBlocking {
            try {
                val res = searchMethod(pref.accessToken,targetId, null)
                val intent = Intent(this@AccountPageActivity, AccountListActivity::class.java).also {
                    it.putParcelableArrayListExtra(
                        AccountListActivity.IntentKey.LIST.name,
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