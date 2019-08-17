package com.nanoyatsu.nastodon.model

import android.content.Context
import android.content.SharedPreferences
import retrofit2.http.Url
import java.net.URL
import java.net.URLEncoder

class AuthPreferenceManager(context: Context) {
    enum class Key {
        NastodonAuth,
        InstanceUrl,
        ClientId,
        ClientSecret,
        AccessToken,
        AccessTokenCreatedAt,
    }

    private val pref: SharedPreferences =
        context.getSharedPreferences(Key.NastodonAuth.name, Context.MODE_PRIVATE)
    private val prefEditor: SharedPreferences.Editor = pref.edit()

    var instanceUrl: String
        get() = pref.getString(Key.InstanceUrl.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.InstanceUrl.name, value)
            prefEditor.apply()
        }

    var clientId: String
        get() = pref.getString(Key.ClientId.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.ClientId.name, value)
            prefEditor.apply()
        }

    var clientSecret: String
        get() = pref.getString(Key.ClientSecret.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.ClientSecret.name, value)
            prefEditor.apply()
        }

    var accessToken: String
        get() = pref.getString(Key.AccessToken.name, "") ?: ""
        set(value) {
            if (value != "") {
                prefEditor.putString(Key.AccessToken.name, "Bearer $value")
//                prefEditor.putString(Key.AccessToken.name, "Bearer+$value")
                prefEditor.apply()
            }
        }

    var accessTokenCreatedAt: Int
        get() = pref.getInt(Key.AccessTokenCreatedAt.name, 0)
        set(value) {
            if (value != 0) {
                prefEditor.putInt(Key.AccessTokenCreatedAt.name, value)
                prefEditor.apply()
            }
        }
}