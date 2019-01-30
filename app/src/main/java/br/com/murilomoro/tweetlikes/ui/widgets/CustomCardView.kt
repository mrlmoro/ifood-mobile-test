package br.com.murilomoro.tweetlikes.ui.widgets

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout

/**
 * Created by Murilo Moro on 26/01/19.
 */
class CustomCardView(
    context: Context,
    attributeSet: AttributeSet
) : CardView(context, attributeSet) {

    override fun onInterceptTouchEvent(ev: MotionEvent?) = true
}