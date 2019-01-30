package br.com.murilomoro.tweetlikes.domain

import br.com.murilomoro.tweetlikes.data.repository.TwitterRepository
import br.com.murilomoro.tweetlikes.domain.model.TweetSentiment
import br.com.murilomoro.tweetlikes.domain.model.TweetsErrorType.*
import br.com.murilomoro.tweetlikes.domain.model.TweetsException
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.Single
import retrofit2.HttpException

/**
 * Created by Murilo Moro on 26/01/19.
 */
interface TwitterUseCase {

    fun getLastTweetsByUsername(name: String): Single<List<Tweet>>

    fun getLastUsernameSearched(): Single<String>

    fun getTweetSentiment(tweet: Tweet): Single<TweetSentiment>
}

class TwitterUseCaseImpl(
    private val repository: TwitterRepository
) : TwitterUseCase {

    override fun getLastTweetsByUsername(name: String): Single<List<Tweet>> {
        return repository.getLastTweetsByUsername(name)
            .onErrorResumeNext {
                Single.error(
                    TweetsException(
                        if (it is HttpException && it.code() == 404) {
                            NOT_FOUND
                        } else {
                            UNKNOW
                        }
                    )
                )
            }
            .map {
                if (it.isNotEmpty()) {
                    return@map it
                } else {
                    throw TweetsException(EMPTY)
                }
            }
    }

    override fun getLastUsernameSearched(): Single<String> {
        return repository.getLastUserSearched()
            .map { it.screenName }
    }

    override fun getTweetSentiment(tweet: Tweet): Single<TweetSentiment> {
        return repository.analyzeTweetSentiment(tweet)
            .onErrorResumeNext { Single.error(TweetsException()) }
            .map {
                when {
                    it.score > 0.25f -> TweetSentiment.HAPPY
                    it.score > -0.25f -> TweetSentiment.NEUTRAL
                    else -> TweetSentiment.SAD
                }
            }
    }

}