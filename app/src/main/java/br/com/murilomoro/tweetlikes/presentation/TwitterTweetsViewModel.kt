package br.com.murilomoro.tweetlikes.presentation

import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import br.com.murilomoro.tweetlikes.domain.TwitterUseCase
import br.com.murilomoro.tweetlikes.domain.model.TweetSentiment
import br.com.murilomoro.tweetlikes.domain.model.TweetsException
import br.com.murilomoro.tweetlikes.ui.base.BaseViewModel
import br.com.murilomoro.tweetlikes.utils.SingleLiveData
import br.com.murilomoro.tweetlikes.utils.getNotNull
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.disposables.Disposable

/**
 * Created by Murilo Moro on 26/01/19.
 */
class TwitterTweetsViewModel(
    private val twitterUseCase: TwitterUseCase
) : BaseViewModel() {

    companion object {
        const val DEFAULT_USERNAME = "iFood"
    }

    val userName = ObservableField<String>("")

    val tweets = MutableLiveData<List<Tweet>>()

    val tweetsError = SingleLiveData<TweetsException>()

    val tweetSentiment = SingleLiveData<TweetSentiment>()

    val tweetSentimentError = SingleLiveData<TweetsException>()

    val showLoading = ObservableBoolean()

    val showSentimentLoading = ObservableBoolean()

    val currentBackgroundColor = ObservableInt()

    private var tweetSentimentDisposable: Disposable? = null

    fun init() {
        subscribe(
            observable = twitterUseCase.getLastUsernameSearched(),
            success = { setUsernameAndSearch(it) },
            error = { setUsernameAndSearch(DEFAULT_USERNAME) }
        )
    }

    private fun setUsernameAndSearch(name: String) {
        userName.set(name)
        searchLastTweets(name)
    }

    fun searchLastTweets(name: String) {
        subscribe(
            observable = twitterUseCase.getLastTweetsByUsername(name)
                .doOnSubscribe { showLoading.set(true) }
                .doFinally { showLoading.set(false) },
            success = {
                userName.set(name)
                tweets.postValue(it)
            },
            error = { tweetsError.postValue(it as TweetsException) }
        )
    }

    fun onRefreshTweets() {
        searchLastTweets(userName.getNotNull())
    }

    fun analyzeTweetSentiment(tweet: Tweet) {
        tweetSentiment.value = null

        dispose(tweetSentimentDisposable)

        tweetSentimentDisposable = subscribe(
            observable = twitterUseCase.getTweetSentiment(tweet)
                .doOnSubscribe { showSentimentLoading.set(true) }
                .doFinally { showSentimentLoading.set(false) },
            success = { tweetSentiment.postValue(it) },
            error = { tweetSentimentError.postValue(it as TweetsException) }
        )
    }

    fun updateSentimentDialog() {
        tweetSentiment.value?.let {
            tweetSentiment.postValue(it)
        }
    }
}