package com.nanoyatsu.nastodon.view.accountList

import android.content.Context
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.data.domain.Account
import com.nanoyatsu.nastodon.view.accountDetail.AccountPageActivity

class AccountsAdapter(private val context: Context, private val accounts: ArrayList<Account>) :
    RecyclerView.Adapter<AccountsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val account = LayoutInflater.from(context)
            .inflate(R.layout.card_account, parent, false) as ConstraintLayout
        return ViewHolder(
            account
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.displayName.text = accounts[position].displayName
        holder.username.text = accounts[position].username
        holder.note.text = Html.fromHtml(accounts[position].note, Html.FROM_HTML_MODE_COMPACT)

        Glide.with(this.context).load(accounts[position].avatarStatic).circleCrop().into(holder.accountAvatar)

        holder.accountAvatar.setOnClickListener { transAccountPage(it, accounts[position]) }
    }

    private fun transAccountPage(v: View, account: Account) {
        val intent = Intent(context, AccountPageActivity::class.java)
            .also { it.putExtra(AccountPageActivity.IntentKey.ACCOUNT.name, account) }
        v.context.startActivity(intent)
    }

    class ViewHolder(account: ConstraintLayout) : RecyclerView.ViewHolder(account) {
        val displayName: TextView = account.findViewById(R.id.displayName)
        val username: TextView = account.findViewById(R.id.username)
        val note: TextView = account.findViewById(R.id.content)
        val accountAvatar: ImageView = account.findViewById(R.id.accountAvatar)
    }
}