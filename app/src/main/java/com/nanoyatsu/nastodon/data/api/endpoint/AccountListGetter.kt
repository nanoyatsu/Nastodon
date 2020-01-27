package com.nanoyatsu.nastodon.data.api.endpoint

import com.nanoyatsu.nastodon.data.domain.Account
import java.io.Serializable

// ジェネリクスいけそう // todo 削除
class AccountListGetter(
    val getMethod: () -> Array<Account>
) : Serializable {
    operator fun invoke(): Array<Account> {
        return getMethod()
    }
}