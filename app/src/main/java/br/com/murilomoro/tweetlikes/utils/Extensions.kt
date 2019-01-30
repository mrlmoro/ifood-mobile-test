package br.com.murilomoro.tweetlikes.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.databinding.ObservableField
import android.os.Build
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast

/**
 * Created by Murilo Moro on 26/01/19.
 */

fun <T> LiveData<T>.observeNotNull(
    owner: LifecycleOwner,
    result: (T) -> Unit
) {
    this.observe(owner, Observer {
        it?.let(result)
    })
}

fun ObservableField<String>.getNotNull() = this.get() ?: ""

fun Context.showToast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG
    ).show()
}

fun Context.getColorFromResource(colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Window.changeNavAndStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = color
        navigationBarColor = color
    }
}

fun View.alphaAnimate(
    alpha: Float = 1f,
    duration: Long = 300,
    endAnim: () -> Unit
) {
    animate()
        .alpha(alpha)
        .withEndAction(endAnim)
        .setDuration(300)
        .start()
}

fun View.startBlinkAnimation() {
    alphaAnimate {
        alphaAnimate(0f) {
            startBlinkAnimation()
        }
    }
}
