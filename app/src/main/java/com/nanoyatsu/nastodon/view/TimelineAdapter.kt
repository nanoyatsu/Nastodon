package com.nanoyatsu.nastodon.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.nanoyatsu.nastodon.R
import com.nanoyatsu.nastodon.model.Status
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request


class TimelineAdapter(context: Context, resource: Int, private val toots: Array<Status>) :
    ArrayAdapter<Status>(context, resource, toots) {
    var compositeDisposable = CompositeDisposable()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val thisView = convertView ?: inflater.inflate(R.layout.toot_card, parent, false)

        thisView.findViewById<ImageView>(R.id.accountAvatar).setImageResource(R.mipmap.ic_sync_problem)
        thisView.findViewById<TextView>(R.id.displayName)?.text = toots[position].account.displayName
        thisView.findViewById<TextView>(R.id.username)?.text = toots[position].account.username
        thisView.findViewById<TextView>(R.id.statusContent)?.text = Html.fromHtml(toots[position].content, Html.FROM_HTML_MODE_COMPACT)

        val request = Request.Builder().let {
            it.url(toots[position].account.avatar)
            it.get()
            it.build()
        }
        val disposable = Single.create<Bitmap> {
            try {
                val response = OkHttpClient().newCall(request).execute()
                if (response.isSuccessful)
                    it.onSuccess(BitmapFactory.decodeStream(response.body()?.byteStream()))
            } catch (e: Exception) {
                e.printStackTrace()
                it.onError(Throwable("user avatar load error"))
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { bitmap ->
                thisView.findViewById<ImageView>(R.id.accountAvatar).setImageBitmap(bitmap)
            }

        compositeDisposable.add(disposable)

        return thisView
    }

    override fun clear() {
        super.clear()
        compositeDisposable.dispose()
    }
}