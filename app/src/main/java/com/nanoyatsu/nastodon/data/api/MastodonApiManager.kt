package com.nanoyatsu.nastodon.data.api

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nanoyatsu.nastodon.data.api.endpoint.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MastodonApiManager(baseUrl: String) {
    private val gson: Gson
    private val retrofit: Retrofit
    private val basePathV1 = "api/v1/"

    init {
        val logging = HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder().also {
            it.addInterceptor(logging)
        }
        gson = GsonBuilder().let {
            it.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            it.create()
        }
        retrofit = Retrofit.Builder().let {
            val fullUrl =
                "https://${if (baseUrl.isEmpty()) "a" else baseUrl}/" // "a"よりも妥当な退避文字あるいは方法
            it.baseUrl(fullUrl)
//            it.baseUrl(baseUrl + basePathV1)
            it.client(httpClient.build())
            it.addConverterFactory(GsonConverterFactory.create(gson))
            it.build()
        }
    }

    val api: MastodonApi
        get() = retrofit.create(MastodonApi::class.java)

    // todo oauthはMastodonの持ち物じゃないのでパスが"{$baseUrl}oauth/token"とか
//    val oauth:

    // todo コメントアウトしたやつは未着手
    val accounts: MastodonApiAccounts
        get() = retrofit.create(MastodonApiAccounts::class.java)
    val apps: MastodonApiApps
        get() = retrofit.create(MastodonApiApps::class.java)
    // val blocks: MastodonApiblocks
//    get() = retrofit.create(MastodonApiblocks::class.java)
// val custom_emojis: MastodonApicustom_emojis
//    get() = retrofit.create(MastodonApicustom_emojis::class.java)
// val domain_blocks: MastodonApidomain_blocks
//    get() = retrofit.create(MastodonApidomain_blocks::class.java)
// val endorsements: MastodonApiendorsements
//    get() = retrofit.create(MastodonApiendorsements::class.java)
    val favourites: MastodonApiFavourites
        get() = retrofit.create(MastodonApiFavourites::class.java)
    // val filters: MastodonApifilters
//    get() = retrofit.create(MastodonApifilters::class.java)
// val follow_requests: MastodonApifollow_requests
//    get() = retrofit.create(MastodonApifollow_requests::class.java)
// val suggestions: MastodonApisuggestions
//    get() = retrofit.create(MastodonApisuggestions::class.java)
    val instance: MastodonApiInstance
        get() = retrofit.create(MastodonApiInstance::class.java)
    // val lists: MastodonApilists
//    get() = retrofit.create(MastodonApilists::class.java)
// val media: MastodonApimedia
//    get() = retrofit.create(MastodonApimedia::class.java)
// val mutes: MastodonApimutes
//    get() = retrofit.create(MastodonApimutes::class.java)
    val notifications: MastodonApiNotifications
        get() = retrofit.create(MastodonApiNotifications::class.java)
    // val polls: MastodonApipolls
//    get() = retrofit.create(MastodonApipolls::class.java)
// val reports: MastodonApireports
//    get() = retrofit.create(MastodonApireports::class.java)
// val scheduled_statuses: MastodonApischeduled_statuses
//    get() = retrofit.create(MastodonApischeduled_statuses::class.java)
// val search: MastodonApisearch
//    get() = retrofit.create(MastodonApisearch::class.java)
    val statuses: MastodonApiStatuses
        get() = retrofit.create(MastodonApiStatuses::class.java)
    val timelines: MastodonApiTimelines
        get() = retrofit.create(MastodonApiTimelines::class.java)


    // いらなさそう
//    enum class Dir(val path: String) {
//        ACCOUNTS("accounts"),
//        APPS("apps"),
//        //        BLOCKS("blocks"),
////        CUSTOM_EMOJI("custom_emojis"),
////        DOMAIN_BLOCKS("domain_blocks"),
////        ENDORSEMENTS("endorsements"),
////        FAVOURITES("favourites"),
////        FILTERS("filters"),
////        FOLLOW_REQUESTS("follow_requests"),
////        FOLLOW_SUGGESTIONS("suggestions"),
//        INSTANCES("instance"),
//        //        LISTS("lists"),
////        MEDIA_ATTACHMENTS("media"),
////        MUTES("mutes"),
////        NOTIFICATIONS("notifications"),
////        POLLS("polls"),
////        REPORTS("reports"),
////        SCHEDULED_STATUSES("scheduled_statuses"),
////        SEARCH("search"),
//        STATUSES("statuses"),
//        TIMELINES("timelines");
//    }


}