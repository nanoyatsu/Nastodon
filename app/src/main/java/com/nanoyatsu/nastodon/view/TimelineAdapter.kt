package com.nanoyatsu.nastodon.view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Status

class TimelineAdapter(context: Context, resource: Int, val toots: Array<Status>) :
    ArrayAdapter<Status>(context, resource, toots) {
//    init {
//
//    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = convertView ?: inflater.inflate(R.layout.toot_card, parent, false)

        thisView.findViewById<ImageView>(R.id.accountAvatar)?.setImageURI(Uri.parse(toots[position].account.avatarStatic)) // fixme ビットマップじゃないから動いてない
        thisView.findViewById<TextView>(R.id.tootUsername)?.text = toots[position].account.username
        thisView.findViewById<TextView>(R.id.accountId)?.text = toots[position].id // fixme これトゥートのIDや
        thisView.findViewById<TextView>(R.id.statusContent)?.text = toots[position].content

        return thisView
    }
}