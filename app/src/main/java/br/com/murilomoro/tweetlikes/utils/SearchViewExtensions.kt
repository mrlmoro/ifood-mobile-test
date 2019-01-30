package br.com.murilomoro.tweetlikes.utils

import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.widget.EditText
import android.widget.ImageView
import br.com.murilomoro.tweetlikes.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

/**
 * Created by Murilo Moro on 26/01/19.
 */

fun SearchView.observe(): Observable<String> {
    val subject = PublishSubject.create<String>()

    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextChange(text: String?): Boolean {
            subject.onNext(text ?: "")
            return true
        }

        override fun onQueryTextSubmit(text: String?): Boolean {
            subject.onComplete()
            return true
        }
    })

    return subject
}

fun SearchView.customStyle() {
    val btClose = findViewById<ImageView>(android.support.v7.appcompat.R.id.search_close_btn)
    btClose.setImageResource(R.drawable.ic_close)

    val tvSearch = findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
    tvSearch.hint = context.getString(R.string.search)
    tvSearch.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
    tvSearch.setHintTextColor(ContextCompat.getColor(context, R.color.colorAccent))
}