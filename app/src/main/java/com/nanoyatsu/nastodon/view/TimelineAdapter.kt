package com.nanoyatsu.nastodon.view

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.nanoyatsu.nastodon.presenter.getImageAsync

class TimelineAdapter(context: Context, resource: Int, private val toots: Array<Status>) :
    ArrayAdapter<Status>(context, resource, toots) {
    private var jobMap: MutableMap<Int,Job> = mutableMapOf()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = convertView ?: inflater.inflate(R.layout.toot_card, parent, false)

        thisView.findViewById<TextView>(R.id.displayName)?.text = toots[position].account.displayName
        thisView.findViewById<TextView>(R.id.username)?.text = toots[position].account.username
        thisView.findViewById<TextView>(R.id.statusContent)?.text =
            Html.fromHtml(toots[position].content, Html.FROM_HTML_MODE_COMPACT)

        val avatar = thisView.findViewById<ImageView>(R.id.accountAvatar)
        avatar.setImageResource(R.mipmap.ic_sync_problem)

        jobMap[position]?.cancel() // fixme 過去の処理が残る対策に入れたけど意味なさそう
        jobMap[position] = GlobalScope.launch(Dispatchers.Main) {
            // todo キャッシュ
            val image = getImageAsync(toots[position].account.avatarStatic).await()
            avatar.setImageBitmap(image)
        }

        return thisView
    }
}