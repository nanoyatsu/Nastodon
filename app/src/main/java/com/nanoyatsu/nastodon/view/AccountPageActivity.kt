package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.model.AuthPreferenceManager
import com.nanoyatsu.nastodon.presenter.AccountListGetter
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
        // fixme トークン必要があとから判明したりして書き方がひどめ
        followingCount.also {
            it.text = getString(R.string.accountFollowingCountFormat, account.followingCount)
//            it.setOnClickListener { transAccountList(pref, account.id, api::getFollowingById, "フォロー一覧") }
        }

        followersCount.also {
            it.text = getString(R.string.accountFollowersCountFormat, account.followersCount)
//            it.setOnClickListener { transAccountList(pref, account.id, api::getFollowersById, "フォロワー一覧") }
        }
    }

    private fun transAccountList(
        pref: AuthPreferenceManager,
        targetId: String,
        searchApi: KSuspendFunction3<String, String, Int?, Response<Array<Account>>>,
        title: String
    ) {
        val func = {
            runBlocking {
                try {
                    val res = searchApi(pref.accessToken, targetId, null)
                    res.body()?: arrayOf()
                } catch (e: HttpException) {
                    e.printStackTrace()
                    arrayOf<Account>()
                }
            }
        }
//        val getter = AccountListGetter(func)
        val intent = Intent(this@AccountPageActivity, AccountListActivity::class.java).also {
            it.putExtra(AccountListActivity.IntentKey.TITLE.name, title)
            it.putExtra(AccountListActivity.IntentKey.GETTER.name, func as java.io.Serializable)
        }
        // fixme Parcelable encountered IOException writing serializable object なので、Serializeできていない。
        //  https://discuss.kotlinlang.org/t/are-closures-serializable/1620 らしいので直接キャストして渡せるかと思ったけどだめです

        startActivity(intent)
    }
}