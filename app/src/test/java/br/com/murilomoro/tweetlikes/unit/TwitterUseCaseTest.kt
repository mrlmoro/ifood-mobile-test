package br.com.murilomoro.tweetlikes.unit

import br.com.murilomoro.tweetlikes.data.repository.TwitterRepository
import br.com.murilomoro.tweetlikes.domain.TwitterUseCase
import br.com.murilomoro.tweetlikes.domain.TwitterUseCaseImpl
import br.com.murilomoro.tweetlikes.domain.model.TweetSentiment
import br.com.murilomoro.tweetlikes.domain.model.TweetsErrorType
import br.com.murilomoro.tweetlikes.domain.model.TweetsException
import br.com.murilomoro.tweetlikes.mock.getSentimentEntityMock
import br.com.murilomoro.tweetlikes.mock.getTweetMock
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito.`when`
import retrofit2.HttpException

/**
 * Created by Murilo Moro on 29/01/19.
 */
class TwitterUseCaseTest : BaseUnitTest() {

    companion object {
        private const val MOCK_NAME = "MOCK_NAME"
        private val MOCK_TWEET = getTweetMock()
        private val MOCK_TWEETS = listOf(MOCK_TWEET)
    }

    private val repositoryMock: TwitterRepository = mock()

    private lateinit var useCase: TwitterUseCase

    override fun `Create system under test`() {
        useCase = TwitterUseCaseImpl(repositoryMock)
    }

    @Test
    fun `Test getLastTweetsByUsername() when repository return a valid tweet list`() {
        //Prepare
        `when`(repositoryMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(MOCK_TWEETS))

        //Action
        val testObserver = useCase.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        testObserver.assertNoErrors()
        testObserver.assertValueCount(1)
        testObserver.assertValue { it.first().idStr == MOCK_TWEET.idStr }
    }

    @Test
    fun `Test getLastTweetsByUsername() when repository return a empty tweet list`() {
        //Prepare
        `when`(repositoryMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.just(listOf()))

        //Action
        val testObserver = useCase.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        testObserver.assertError {
            it is TweetsException && it.errorType == TweetsErrorType.EMPTY
        }
    }

    @Test
    fun `Test getLastTweetsByUsername() when repository return user not found exception`() {
        //Prepare
        mockHttpException(404)

        //Action
        val testObserver = useCase.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        testObserver.assertError {
            it is TweetsException && it.errorType == TweetsErrorType.NOT_FOUND
        }
    }

    @Test
    fun `Test getLastTweetsByUsername() when repository return http exception with unknow code`() {
        //Prepare
        mockHttpException(500)

        //Action
        val testObserver = useCase.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        testObserver.assertError {
            it is TweetsException && it.errorType == TweetsErrorType.UNKNOW
        }
    }

    @Test
    fun `Test getLastTweetsByUsername() when repository return unknow exception`() {
        //Prepare
        `when`(repositoryMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.error(Throwable()))

        //Action
        val testObserver = useCase.getLastTweetsByUsername(MOCK_NAME).test()

        //Verify
        testObserver.assertError {
            it is TweetsException && it.errorType == TweetsErrorType.UNKNOW
        }
    }

    @Test
    fun `Test getLastUsernameSearched() data converter from user to username`() {
        //Prepare
        `when`(repositoryMock.getLastUserSearched())
            .thenReturn(Single.just(MOCK_TWEET.user))

        //Action
        val testObserver = useCase.getLastUsernameSearched().test()

        //Verify
        testObserver.assertResult(MOCK_TWEET.user.screenName)
    }

    @Test
    fun `Test getTweetSentiment() when repository return some exception`() {
        //Prepare
        `when`(repositoryMock.analyzeTweetSentiment(MOCK_TWEET))
            .thenReturn(Single.error(Throwable()))

        //Action
        val testObserver = useCase.getTweetSentiment(MOCK_TWEET).test()

        //Verify
        testObserver.assertError(TweetsException::class.java)
    }

    @Test
    fun `Test getTweetSentiment() when repository return score to HAPPY`() {
        `Test getTweetSentiment() when repository return score`(0.26f, TweetSentiment.HAPPY)
    }

    @Test
    fun `Test getTweetSentiment() when repository return score to NEUTRAL`() {
        `Test getTweetSentiment() when repository return score`(0.25f, TweetSentiment.NEUTRAL)
    }

    @Test
    fun `Test getTweetSentiment() when repository return score to SAD`() {
        `Test getTweetSentiment() when repository return score`(-0.25f, TweetSentiment.SAD)
    }

    private fun `Test getTweetSentiment() when repository return score`(
        score: Float,
        expectedValue: TweetSentiment
    ) {
        //Prepare
        `when`(repositoryMock.analyzeTweetSentiment(MOCK_TWEET))
            .thenReturn(Single.just(getSentimentEntityMock(score)))

        //Action
        val testObserver = useCase.getTweetSentiment(MOCK_TWEET).test()

        //Verify
        testObserver.assertValue(expectedValue)
    }

    private fun mockHttpException(code: Int) {
        val httpExceptionMock = mock<HttpException>()
        `when`(httpExceptionMock.code()).thenReturn(code)

        `when`(repositoryMock.getLastTweetsByUsername(MOCK_NAME))
            .thenReturn(Single.error(httpExceptionMock))
    }
}