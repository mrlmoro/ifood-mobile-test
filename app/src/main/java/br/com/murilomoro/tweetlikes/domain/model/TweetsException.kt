package br.com.murilomoro.tweetlikes.domain.model

import br.com.murilomoro.tweetlikes.domain.model.TweetsErrorType

/**
 * Created by Murilo Moro on 26/01/19.
 */
class TweetsException(
    val errorType: TweetsErrorType = TweetsErrorType.UNKNOW
) : Exception()