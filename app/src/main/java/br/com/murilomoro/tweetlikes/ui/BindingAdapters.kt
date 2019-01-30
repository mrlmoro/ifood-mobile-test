package br.com.murilomoro.tweetlikes.ui

import android.databinding.BindingAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import br.com.murilomoro.tweetlikes.utils.startBlinkAnimation

/**
 * Created by Murilo Moro on 27/01/19.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("show")
    fun View.show(show: Boolean) {
        visibility = if (show) VISIBLE else GONE
    }

    @JvmStatic
    @BindingAdapter("backgroundColorId")
    fun View.backgroundColorId(colorRes: Int) {
        setBackgroundResource(colorRes)
    }

    @JvmStatic
    @BindingAdapter("blinkAnimation")
    fun View.blinkAnimation(show: Boolean) {
        if (show) {
            startBlinkAnimation()
        } else {
            animate().cancel()
            alpha = 1f
        }
    }
}