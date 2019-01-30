package br.com.murilomoro.tweetlikes.data.remote

import android.content.Context
import br.com.murilomoro.tweetlikes.R
import br.com.murilomoro.tweetlikes.utils.Logger
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.language.v1.CloudNaturalLanguage
import com.google.api.services.language.v1.CloudNaturalLanguageScopes
import com.google.api.services.language.v1.model.AnalyzeSentimentRequest
import com.google.api.services.language.v1.model.Document
import com.google.api.services.language.v1.model.Sentiment
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.io.IOException

/**
 * Created by Murilo Moro on 27/01/19.
 */
interface GoogleNaturalLanguageApi {

    fun getSentimentAnalyze(text: String): Single<Sentiment>
}

class GoogleNaturalLanguageApiImpl(
    private val context: Context,
    private val logger: Logger
) : GoogleNaturalLanguageApi {

    private var googleCredential: GoogleCredential? = null

    companion object {
        const val TAG = "GoogleNaturalLanguageApi"
    }

    private val googleApi = CloudNaturalLanguage.Builder(
        NetHttpTransport(),
        JacksonFactory.getDefaultInstance(),
        HttpRequestInitializer {
            generateCredentialAccessToken()
            googleCredential?.initialize(it)
        }
    ).build()

    override fun getSentimentAnalyze(text: String): Single<Sentiment> {
        return Single.create<Sentiment> { emitter ->
            val request = AnalyzeSentimentRequest()
                .setDocument(
                    Document()
                        .setContent(text)
                        .setType("PLAIN_TEXT")
                )

            try {
                callGoogleAnalyzeSentiment(request, emitter)
            } catch (ex: GoogleJsonResponseException) {
                //If accessToken is invalid/expired try to refresh call with new token generated
                if (ex.details.code == 401) {
                    googleCredential?.accessToken = null
                    callGoogleAnalyzeSentiment(request, emitter)
                }
            }

        }.doOnError {
            logger.error(TAG, "Error: ${it.message}")
        }
    }

    @Throws(IOException::class)
    private fun callGoogleAnalyzeSentiment(
        request: AnalyzeSentimentRequest,
        emitter: SingleEmitter<Sentiment>
    ) {
        googleApi
            .documents()
            .analyzeSentiment(request)
            .execute()
            .documentSentiment
            ?.let {
                emitter.onSuccess(it)
            }
            ?: emitter.onError(NullPointerException())
    }

    private fun generateCredentialAccessToken() {
        val currentAccessToken = googleCredential?.accessToken
        if (currentAccessToken?.isNotEmpty() == true) {
            logger.info(TAG, "Current AccessToken: $currentAccessToken")
            return
        }

        context.resources.openRawResource(R.raw.google_credential)
            .let {
                val credential = GoogleCredential.fromStream(it)
                    .createScoped(CloudNaturalLanguageScopes.all())

                try {
                    credential.refreshToken()

                    googleCredential = GoogleCredential()
                        .setAccessToken(credential.accessToken)
                        .createScoped(CloudNaturalLanguageScopes.all())

                    logger.info(TAG, "Generate AccessToken success: ${googleCredential?.accessToken}")
                } catch (ex: IOException) {
                    logger.info(TAG, "Generate AccessToken error: ${ex.message}")
                }
            }
    }

}