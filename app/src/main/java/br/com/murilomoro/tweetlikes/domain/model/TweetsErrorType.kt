package br.com.murilomoro.tweetlikes.domain.model

import br.com.murilomoro.tweetlikes.R

/**
 * Created by Murilo Moro on 26/01/19.
 */
enum class TweetsErrorType(
    val resId: Int
) {
    UNKNOW(R.string.unexpected_error),
    EMPTY(R.string.username_empty_tweets),
    NOT_FOUND(R.string.username_not_found)
}