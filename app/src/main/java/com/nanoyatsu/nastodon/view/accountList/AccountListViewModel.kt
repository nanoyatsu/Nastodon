package com.nanoyatsu.nastodon.view.accountList

import androidx.lifecycle.ViewModel
import com.nanoyatsu.nastodon.data.api.endpoint.MastodonApiAccounts
import com.nanoyatsu.nastodon.data.api.entity.APIAccount
import com.nanoyatsu.nastodon.data.repository.account.AccountRepository
import retrofit2.Response

class AccountListViewModel(
    private val repo: AccountRepository
) : ViewModel() {
    enum class Kind(val getter: (suspend (MastodonApiAccounts, String, String, String?, String?) -> Response<List<APIAccount>>)) {
        FOLLOWING(::followingApiProvider),
        FOLLOWER(::followerApiProvider),
        MUTING(::followingApiProvider), // 仮
        BLOCKING(::followingApiProvider) // 仮
    }

    companion object {
        suspend fun followingApiProvider(
            apiDir: MastodonApiAccounts,
            accountId: String,
            token: String,
            maxId: String?,
            sinceId: String?
        ) = apiDir.getFollowingById(token, accountId, maxId, sinceId)

        suspend fun followerApiProvider(
            apiDir: MastodonApiAccounts,
            accountId: String,
            token: String,
            maxId: String?,
            sinceId: String?
        ) = apiDir.getFollowersById(token, accountId, maxId, sinceId)
    }
}