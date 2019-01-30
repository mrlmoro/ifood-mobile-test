package br.com.murilomoro.tweetlikes.data.repository

import android.arch.persistence.room.EmptyResultSetException
import br.com.murilomoro.tweetlikes.data.UserNotCachedException
import br.com.murilomoro.tweetlikes.data.local.Preferences
import br.com.murilomoro.tweetlikes.data.local.sentiment.SentimentDao
import br.com.murilomoro.tweetlikes.data.local.sentiment.SentimentEntity
import br.com.murilomoro.tweetlikes.data.remote.GoogleNaturalLanguageApi
import br.com.murilomoro.tweetlikes.data.remote.TwitterService
import br.com.murilomoro.tweetlikes.utils.Logger
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

/**
 * Created by Murilo Moro on 26/01/19.
 */
interface TwitterRepository {

    fun getLastTweetsByUsername(name: String): Single<List<Tweet>>

    fun getLastUserSearched(): Single<User>

    fun analyzeTweetSentiment(tweet: Tweet): Single<SentimentEntity>
}

class TwitterRepositoryImpl(
    private val service: TwitterService,
    private val languageApi: GoogleNaturalLanguageApi,
    private val preferences: Preferences,
    private val sentimentDao: SentimentDao,
    private val logger: Logger
) : TwitterRepository {

    companion object {
        const val TAG = "TwitterRepository"
        const val LAST_USER_KEY = "LAST_USER_KEY"
    }

    override fun getLastTweetsByUsername(name: String): Single<List<Tweet>> {
        return service.getLastTweetsByUsername(name)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                if (it.isNotEmpty()) {
                    preferences.putData(
                        LAST_USER_KEY,
                        it.first().user
                    )
                }
            }
    }

    override fun getLastUserSearched(): Single<User> {
        return Single.create<User> { emitter ->
            preferences.getData(LAST_USER_KEY, User::class.java)
                ?.let {
                    emitter.onSuccess(it)
                } ?: emitter.onError(UserNotCachedException())
        }.subscribeOn(Schedulers.io())
    }

    override fun analyzeTweetSentiment(tweet: Tweet): Single<SentimentEntity> {
        return sentimentDao.find(tweet.idStr)
            .subscribeOn(Schedulers.io())
            .onErrorResumeNext {
                when (it) {
                    is EmptyResultSetException -> analyzeSentimentOnServer(tweet)
                    else -> Single.error(it)
                }
            }
            .doOnError { logger.error(TAG, "analyze error = ${it.message}") }
            .doOnSuccess { logger.info(TAG, it.toString()) }
    }

    private fun analyzeSentimentOnServer(tweet: Tweet): Single<SentimentEntity> {
        return languageApi.getSentimentAnalyze(tweet.text)
            .map {
                val sentimentEntity = SentimentEntity(
                    tweet.idStr,
                    it.score,
                    it.magnitude
                )

                sentimentDao.insert(sentimentEntity)

                sentimentEntity
            }
    }

}