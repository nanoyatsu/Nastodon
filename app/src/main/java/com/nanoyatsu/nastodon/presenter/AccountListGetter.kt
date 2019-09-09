package com.nanoyatsu.nastodon.presenter

import com.nanoyatsu.nastodon.model.Account
import java.io.Serializable

// ジェネリクスいけそう
class AccountListGetter(
    val getMethod: () -> Array<Account>
) : Serializable {
    operator fun invoke(): Array<Account> {
        return getMethod()
    }
}