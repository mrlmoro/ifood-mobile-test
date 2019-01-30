package br.com.murilomoro.tweetlikes.data.remote

import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Murilo Moro on 26/01/19.
 */
interface TwitterService {

    @GET("/1.1/statuses/user_timeline.json")
    fun getLastTweetsByUsername(
        @Query("screen_name") name: String,
        @Query("count") count: Int = 10
    ): Single<List<Tweet>>
}