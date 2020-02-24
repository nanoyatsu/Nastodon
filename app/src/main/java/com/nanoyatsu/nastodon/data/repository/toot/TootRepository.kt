package com.nanoyatsu.nastodon.data.repository.toot

import com.nanoyatsu.nastodon.data.api.MastodonApiManager
import com.nanoyatsu.nastodon.data.api.entity.APIStatus
import com.nanoyatsu.nastodon.data.database.dao.TimelineDao
import com.nanoyatsu.nastodon.data.database.entity.AuthInfo
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class TootRepository @Inject constructor(
    private val dao: TimelineDao,
    val apiManager: MastodonApiManager,
    val auth: AuthInfo
) {
    private val apiStatuses = apiManager.statuses
    private val apiFavourites = apiManager.favourites
    val token = auth.accessToken

    fun isMyToot(accountId: String) = (accountId == auth.accountId)

    suspend fun doReblog(id: String, reblogged: Boolean) {
        val api = if (reblogged) apiStatuses::unReblog else apiStatuses::reblog
        doStatusApi(suspend { api(auth.accessToken, id) }, { dao.update(it, id) })
    }

    suspend fun doFav(id: String, favourited: Boolean) {
        val api = if (favourited) apiFavourites::unFavourite else apiFavourites::favourite
        doStatusApi(suspend { api(auth.accessToken, id) }, { dao.update(it, id) })
    }

    suspend fun doPin(id: String, pinned: Boolean?) {
        val api = if (pinned == true) apiStatuses::unPin else apiStatuses::pin
        doStatusApi(suspend { api(auth.accessToken, id) }, { dao.update(it, id) })
    }

    private suspend fun doStatusApi(
        api: suspend () -> Response<APIStatus>,
        updater: (String) -> Unit
    ) {
        try {
            val res = api()
            if (res.body() == null) {
                // todo res.errorBody()（JSONパース失敗かと思うので、ここに来る時はたぶん実装ミス）（下位互換切り仕様変更も無いと思いたい）
            }
            val dbModel = requireNotNull(res.body()).asDatabaseModel(0)
            updater(dbModel.status)
        } catch (e: Exception) {
            e.printStackTrace()
            // todo エラー表示
        }
    }

    suspend fun doDelete(id: String) {
        try {
            val res = apiStatuses.deleteToot(auth.accessToken, id)
            requireNotNull(res.body())
            dao.deleteById(id)
        } catch (e: HttpException) {
            TODO("エラー表示")
        } catch (e: IllegalArgumentException) {
            TODO("エラー表示")
        }
    }
}