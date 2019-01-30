package br.com.murilomoro.tweetlikes.robo

import android.arch.lifecycle.Observer
import br.com.murilomoro.tweetlikes.domain.model.TweetSentiment
import br.com.murilomoro.tweetlikes.mock.HAPPY_TEXT
import br.com.murilomoro.tweetlikes.mock.NEUTRAL_TEXT
import br.com.murilomoro.tweetlikes.mock.SAD_TEX
import br.com.murilomoro.tweetlikes.mock.getTweetMock
import br.com.murilomoro.tweetlikes.presentation.TwitterTweetsActivity
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.standalone.StandAloneContext
import org.koin.test.KoinTest
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController

/**
 * Created by Murilo Moro on 29/01/19.
 */
@RunWith(RobolectricTestRunner::class)
class TwitterTweetsActivityTest : KoinTest {

    companion object {
        private val MOCK_HAPPY_TWEET = getTweetMock(HAPPY_TEXT)
        private val MOCK_SAD_TWEET = getTweetMock(SAD_TEX)
        private val MOCK_NEUTRAL_TWEET = getTweetMock(NEUTRAL_TEXT)
    }

    private val observerTweetSentimentMock: Observer<TweetSentiment> = mock()

    private lateinit var controller: ActivityController<TwitterTweetsActivity>

    @Before
    fun `Setup test`() {
        controller = Robolectric.buildActivity(TwitterTweetsActivity::class.java).create()
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun `Finish test`() {
        StandAloneContext.stopKoin()
        RxJavaPlugins.reset()
        controller.destroy()
    }

    @Test
    fun `Test analyzeTweetSentiment() when tweet is HAPPY`() {
        `Test analyzeTweetSentiment()`(
            MOCK_HAPPY_TWEET,
            TweetSentiment.HAPPY
        )
    }

    @Test
    fun `Test analyzeTweetSentiment() when tweet is NEUTRAL`() {
        `Test analyzeTweetSentiment()`(
            MOCK_NEUTRAL_TWEET,
            TweetSentiment.NEUTRAL
        )
    }

    @Test
    fun `Test analyzeTweetSentiment() when tweet is SAD`() {
        `Test analyzeTweetSentiment()`(
            MOCK_SAD_TWEET,
            TweetSentiment.SAD
        )
    }

    private fun `Test analyzeTweetSentiment()`(
        tweetMock: Tweet,
        expectedSentimentMock: TweetSentiment
    ) {
        //Prepare
        val activity = controller.get()
        activity.viewModel.tweetSentiment.observeForever(observerTweetSentimentMock)

        //Action
        activity.onTweetClicked(tweetMock)

        //Verify
        verify(observerTweetSentimentMock).onChanged(null)
        verify(observerTweetSentimentMock).onChanged(expectedSentimentMock)
    }

}