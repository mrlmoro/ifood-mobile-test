package br.com.murilomoro.tweetlikes.unit

import android.arch.persistence.room.EmptyResultSetException
import br.com.murilomoro.tweetlikes.data.UserNotCachedException
import br.com.murilomoro.tweetlikes.data.local.Preferences
import br.com.murilomoro.tweetlikes.data.local.sentiment.SentimentDao
import br.com.murilomoro.tweetlikes.data.remote.GoogleNaturalLanguageApi
import br.com.murilomoro.tweetlikes.data.remote.TwitterService
import br.com.murilomoro.tweetlikes.data.repository.TwitterRepository
import br.com.murilomoro.tweetlikes.data.repository.TwitterRepositoryImpl
import br.com.murilomoro.tweetlikes.data.repository.TwitterRepositoryImpl.Companion.LAST_USER_KEY
import br.com.murilomoro.tweetlikes.mock.getSentimentEntityMock
import br.com.murilomoro.tweetlikes.mock.getSentimentMock
import br.com.murilomoro.tweetlikes.mock.getTweetMock
import br.com.murilomoro.tweetlikes.utils.Logger
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import io.reactivex.Single
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`

/**
 * Created by Murilo Moro on 28/01/19.
 */
class TwitterRepositoryTest : BaseUnitTest() {

    companion object {
        private const val MOCK_NAME = "NAME"
        private val TWEET_MOCK = getTweetMock()
        private val USER_MOCK = TWEET_MOCK.user
    }

    private val serviceMock: TwitterService = mock()

    private val languageApiMock: GoogleNaturalLanguageApi = mock()

    private val preferencesMock: Preferences = mock()

    private val sentimentDaoMock: SentimentDao = mock()

    private val loggerMock: Logger = mock()

    private lateinit var repository: TwitterRepository

    override fun `Create system under test`() {
        repository = TwitterRepositoryImpl(
            serviceMock,
            languageApiMock,
            preferencesMock,
            sentimentDaoMock,
            loggerMock
        )
    }

    @Test
    fun `Test getLastTweetsByUsername() to verify if User inside a Tweet is saved on cache`() {
        //Prepare
        `when`(serviceMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(listOf(TWEET_MOCK)))

        //Action
        repository.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        verify(preferencesMock).putData(eq(LAST_USER_KEY), any<Tweet>())
    }

    @Test
    fun `Test getLastTweetsByUsername() to verify if cache is not called when list is empty`() {
        //Prepare
        `when`(serviceMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(listOf()))

        //Action
        repository.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        verifyZeroInteractions(preferencesMock)
    }

    @Test
    fun `Test getLastUserSearched() when it contains in cache`() {
        //Prepare
        `when`(preferencesMock.getData(LAST_USER_KEY, User::class.java))
            .thenReturn(USER_MOCK)

        //Action
        val testObserver = repository.getLastUserSearched().test()

        //Verify
        testObserver.assertNoErrors()
        testObserver.assertResult(USER_MOCK)
    }

    @Test
    fun `Test getLastUserSearched() when it is not cached`() {
        //Prepare
        `when`(preferencesMock.getData(LAST_USER_KEY, User::class.java))
            .thenReturn(null)

        //Action
        val testObserver = repository.getLastUserSearched().test()

        //Verify
        testObserver.assertError(UserNotCachedException::class.java)
    }

    @Test
    fun `Test analyzeTweetSentiment() when tweet sentiment already persisted`() {
        //Prepare
        val sentimentEntityMock = getSentimentEntityMock(0.5f)

        `when`(sentimentDaoMock.find(TWEET_MOCK.idStr))
            .thenReturn(Single.just(sentimentEntityMock))

        //Action
        val testObserver = repository.analyzeTweetSentiment(TWEET_MOCK).test()

        //Verify
        testObserver.assertNoErrors()
        testObserver.assertResult(sentimentEntityMock)
    }

    @Test
    fun `Test analyzeTweetSentiment() when tweet sentiment is not persisted then language api is called`() {
        //Prepare
        val sentimentMock = getSentimentMock(0.5f)

        `when`(sentimentDaoMock.find(TWEET_MOCK.idStr))
            .thenReturn(Single.error(EmptyResultSetException(any())))

        `when`(languageApiMock.getSentimentAnalyze(TWEET_MOCK.text))
            .thenReturn(Single.just(sentimentMock))

        //Action
        val testObserver = repository.analyzeTweetSentiment(TWEET_MOCK).test()
        val result = testObserver.values().first()

        //Verify
        testObserver.assertValue {
            it.tweetId == TWEET_MOCK.idStr
                    && it.score == sentimentMock.score
                    && it.magnitude == sentimentMock.magnitude
        }
        verify(sentimentDaoMock).insert(result)
    }

}