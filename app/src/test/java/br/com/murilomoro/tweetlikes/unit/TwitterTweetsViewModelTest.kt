package br.com.murilomoro.tweetlikes.unit

import android.arch.lifecycle.Observer
import br.com.murilomoro.tweetlikes.data.UserNotCachedException
import br.com.murilomoro.tweetlikes.domain.TwitterUseCase
import br.com.murilomoro.tweetlikes.domain.model.TweetSentiment
import br.com.murilomoro.tweetlikes.domain.model.TweetsException
import br.com.murilomoro.tweetlikes.mock.getTweetMock
import br.com.murilomoro.tweetlikes.presentation.TwitterTweetsViewModel
import br.com.murilomoro.tweetlikes.presentation.TwitterTweetsViewModel.Companion.DEFAULT_USERNAME
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.Single
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.mockito.Mockito.`when`

/**
 * Created by Murilo Moro on 29/01/19.
 */
class TwitterTweetsViewModelTest : BaseUnitTest() {

    companion object {
        private const val MOCK_NAME = "MOCK_NAME"
        private val MOCK_TWEET = getTweetMock()
        private val MOCK_TWEETS = listOf(MOCK_TWEET)
    }

    private val useCaseMock: TwitterUseCase = mock()

    private val observerTweetsMock: Observer<List<Tweet>> = mock()

    private val observerTweetExceptionMock: Observer<TweetsException> = mock()

    private val observerTweetSentimentMock: Observer<TweetSentiment> = mock()

    private lateinit var viewModel: TwitterTweetsViewModel

    override fun `Create system under test`() {
        viewModel = TwitterTweetsViewModel(useCaseMock)
    }

    @Test
    fun `Test init() when last user searched is found in cache`() {
        //Prepare
        `when`(useCaseMock.getLastUsernameSearched())
            .thenReturn(Single.just(MOCK_NAME))

        `when`(useCaseMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(MOCK_TWEETS))

        //Action
        viewModel.init()

        //Verify
        assertThat(viewModel.userName.get(), `is`(MOCK_NAME))
        verify(useCaseMock).getLastTweetsByUsername(MOCK_NAME)
    }

    @Test
    fun `Test init() when last user searched is not found in cache`() {
        //Prepare
        `when`(useCaseMock.getLastUsernameSearched())
            .thenReturn(Single.error(UserNotCachedException()))

        `when`(useCaseMock.getLastTweetsByUsername(DEFAULT_USERNAME))
            .thenReturn(Single.just(MOCK_TWEETS))

        //Action
        viewModel.init()

        //Verify
        assertThat(viewModel.userName.get(), `is`(DEFAULT_USERNAME))
        verify(useCaseMock).getLastTweetsByUsername(DEFAULT_USERNAME)
    }

    @Test
    fun `Test searchLastTweets() when username is found and have tweets`() {
        //Prepare
        `when`(useCaseMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(MOCK_TWEETS))

        viewModel.tweets.observeForever(observerTweetsMock)

        //Action
        viewModel.searchLastTweets(MOCK_NAME)

        //Verify
        assertThat(viewModel.userName.get(), `is`(MOCK_NAME))
        verify(observerTweetsMock).onChanged(MOCK_TWEETS)
    }

    @Test
    fun `Test searchLastTweets() when exception occur`() {
        //Prepare
        val exceptionMock = TweetsException()
        `when`(useCaseMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.error(exceptionMock))

        viewModel.tweetsError.observeForever(observerTweetExceptionMock)

        //Action
        viewModel.searchLastTweets(MOCK_NAME)

        //Verify
        verify(observerTweetExceptionMock).onChanged(exceptionMock)
    }

    @Test
    fun `Test onRefreshTweets() to verify if current username is used to fetch tweets`() {
        //Prepare
        viewModel.userName.set(MOCK_NAME)

        `when`(useCaseMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(MOCK_TWEETS))

        //Action
        viewModel.onRefreshTweets()

        //Verify
        verify(useCaseMock).getLastTweetsByUsername(MOCK_NAME)
    }

    @Test
    fun `Test analyzeTweetSentiment() when sentiment analyze return success`() {
        //Prepare
        `when`(useCaseMock.getTweetSentiment(MOCK_TWEET))
            .thenReturn(Single.just(TweetSentiment.HAPPY))

        viewModel.tweetSentiment.observeForever(observerTweetSentimentMock)

        //Action
        viewModel.analyzeTweetSentiment(MOCK_TWEET)

        //Verify
        verify(observerTweetSentimentMock).onChanged(TweetSentiment.HAPPY)
    }

    @Test
    fun `Test analyzeTweetSentiment() when sentiment analyze return error`() {
        //Prepare
        val exceptionMock = TweetsException()
        `when`(useCaseMock.getTweetSentiment(MOCK_TWEET))
            .thenReturn(Single.error(exceptionMock))

        viewModel.tweetSentimentError.observeForever(observerTweetExceptionMock)

        //Action
        viewModel.analyzeTweetSentiment(MOCK_TWEET)

        //Verify
        verify(observerTweetExceptionMock).onChanged(exceptionMock)
    }

    @Test
    fun `Test updateSentimentDialog() when tweet sentiment has data`() {
        //Prepare
        viewModel.tweetSentiment.value = TweetSentiment.HAPPY
        viewModel.tweetSentiment.observeForever(observerTweetSentimentMock)

        //Action
        viewModel.updateSentimentDialog()

        //Verify
        verify(
            observerTweetSentimentMock,
            times(2)
        ).onChanged(TweetSentiment.HAPPY)
    }

    @Test
    fun `Test updateSentimentDialog() when tweet sentiment is null`() {
        //Prepare
        viewModel.tweetSentiment.observeForever(observerTweetSentimentMock)

        //Action
        viewModel.updateSentimentDialog()

        //Verify
        verifyZeroInteractions(observerTweetSentimentMock)
    }

}