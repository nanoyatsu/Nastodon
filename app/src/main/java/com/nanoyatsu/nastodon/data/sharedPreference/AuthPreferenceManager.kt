package com.nanoyatsu.nastodon.data.sharedPreference

import android.content.Context
import android.content.SharedPreferences

class AuthPreferenceManager(context: Context) {
    enum class Key {
        NastodonAuth,
        InstanceUrl,
        ClientId,
        ClientSecret,
        AccessToken,
        AccessTokenCreatedAt,
        AccountId,
        AccountUsername,
        AccountDisplayName,
        AccountAvatar,
        AccountHeader
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
            if (value == "") return
            prefEditor.putString(Key.AccessToken.name, "Bearer $value")
            // prefEditor.putString(Key.AccessToken.name, "Bearer+$value")
            prefEditor.apply()
        }

    var accessTokenCreatedAt: Int
        get() = pref.getInt(Key.AccessTokenCreatedAt.name, 0)
        set(value) {
            if (value != 0) {
                prefEditor.putInt(Key.AccessTokenCreatedAt.name, value)
                prefEditor.apply()
            }
        }

    var accountId: String
        get() = pref.getString(Key.AccountId.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.AccountId.name, value)
            prefEditor.apply()
        }

    var accountUsername: String
        get() = pref.getString(Key.AccountUsername.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.AccountUsername.name, value)
            prefEditor.apply()
        }

    var accountDisplayName: String
        get() = pref.getString(Key.AccountDisplayName.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.AccountDisplayName.name, value)
            prefEditor.apply()
        }

    var accountAvatar: String
        get() = pref.getString(Key.AccountAvatar.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.AccountAvatar.name, value)
            prefEditor.apply()
        }

    var accountHeader: String
        get() = pref.getString(Key.AccountHeader.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.AccountHeader.name, value)
            prefEditor.apply()
        }

}