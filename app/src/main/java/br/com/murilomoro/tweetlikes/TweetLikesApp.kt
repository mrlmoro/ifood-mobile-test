package br.com.murilomoro.tweetlikes

import android.app.Application
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig
import org.koin.android.ext.android.startKoin


/**
 * Created by Murilo Moro on 26/01/19.
 */
class TweetLikesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initTwitter()

        startKoin(this, modules)
    }

    private fun initTwitter() {
        val config = TwitterConfig.Builder(this)
            .logger(DefaultLogger(Log.DEBUG))
            .twitterAuthConfig(
                TwitterAuthConfig(
                    getString(R.string.twitter_consumer_key),
                    getString(R.string.twitter_consumer_secret_key)
                )
            )
            .debug(BuildConfig.DEBUG)
            .build()

        Twitter.initialize(config)
    }
}