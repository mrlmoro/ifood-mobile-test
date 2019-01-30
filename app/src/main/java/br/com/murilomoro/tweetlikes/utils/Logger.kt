package br.com.murilomoro.tweetlikes.utils

import android.util.Log
import br.com.murilomoro.tweetlikes.BuildConfig

/**
 * Created by Murilo Moro on 27/01/19.
 */

interface Logger {

    fun info(tag: String, text: String)

    fun error(tag: String, text: String)
}

class LoggerImpl : Logger {

    override fun info(tag: String, text: String) {
        if (BuildConfig.DEBUG)
            Log.i(tag, text)
    }

    override fun error(tag: String, text: String) {
        if (BuildConfig.DEBUG)
            Log.e(tag, text)
    }

}