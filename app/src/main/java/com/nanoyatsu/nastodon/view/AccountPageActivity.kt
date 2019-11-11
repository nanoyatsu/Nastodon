package com.nanoyatsu.nastodon.view

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.NastodonDataBase
import com.nanoyatsu.nastodon.data.dao.AuthInfoDao
import com.nanoyatsu.nastodon.data.entity.AuthInfo
import com.nanoyatsu.nastodon.model.Account
import com.nanoyatsu.nastodon.presenter.MastodonApiManager
import kotlinx.android.synthetic.main.activity_account_page.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import kotlin.reflect.KSuspendFunction3

class AccountPageActivity : AppCompatActivity() {
    enum class IntentKey { ACCOUNT }

    private lateinit var authInfoDao: AuthInfoDao
    private lateinit var auth: AuthInfo
    private lateinit var apiManager: MastodonApiManager

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

        authInfoDao = NastodonDataBase.getInstance().authInfoDao()
        // todo マルチアカウント考慮
        runBlocking(context = Dispatchers.IO) { auth = authInfoDao.getAll().first() }
        if (auth.instanceUrl == "") return // todo 認証に行く
        apiManager = MastodonApiManager(auth.instanceUrl)

        val api = apiManager.api
        // fixme トークン必要があとから判明したりして書き方がひどめ
        followingCount.also {
            it.text = getString(R.string.accountFollowingCountFormat, account.followingCount)
//            it.setOnClickListener { transAccountList(auth, account.id, api::getFollowingById, "フォロー一覧") }
        }

        followersCount.also {
            it.text = getString(R.string.accountFollowersCountFormat, account.followersCount)
//            it.setOnClickListener { transAccountList(auth, account.id, api::getFollowersById, "フォロワー一覧") }
        }
    }

    private fun transAccountList(
        auth: AuthInfo,
        targetId: String,
        searchApi: KSuspendFunction3<String, String, Int?, Response<Array<Account>>>,
        title: String
    ) {
        val func = {
            runBlocking {
                try {
                    val res = searchApi(auth.accessToken, targetId, null)
                    res.body() ?: arrayOf()
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