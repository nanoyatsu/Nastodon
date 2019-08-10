package com.nanoyatsu.nastodon.model

import android.content.Context
import android.content.SharedPreferences

class AuthPreferenceManager(context: Context) {
    enum class Key {
        NastodonAuth,
        InstanceDomain,
        AccessToken,
        ClientId,
        ClientSecret
    }

    private val pref: SharedPreferences =
        context.getSharedPreferences(Key.NastodonAuth.name, Context.MODE_PRIVATE)
    private val prefEditor: SharedPreferences.Editor = pref.edit()

    var instanceDomain: String
        get() = pref.getString(Key.InstanceDomain.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.InstanceDomain.name, value)
            prefEditor.apply()
        }

    var accessToken: String
        get() = pref.getString(Key.AccessToken.name, "") ?: ""
        set(value) {
            prefEditor.putString(Key.AccessToken.name, value)
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
}