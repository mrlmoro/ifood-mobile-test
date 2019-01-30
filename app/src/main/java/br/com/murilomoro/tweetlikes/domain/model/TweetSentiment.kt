package br.com.murilomoro.tweetlikes.domain.model

import br.com.murilomoro.tweetlikes.R

/**
 * Created by Murilo Moro on 27/01/19.
 */
enum class TweetSentiment(
    val emoji: String,
    val colorRes: Int
) {
    HAPPY("\uD83D\uDE03", R.color.emoji_happy),
    NEUTRAL("\uD83D\uDE10", R.color.emoji_neutral),
    SAD("\uD83D\uDE14", R.color.emoji_sad)
}