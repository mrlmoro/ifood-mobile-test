package br.com.murilomoro.tweetlikes.data.local.sentiment

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Murilo Moro on 28/01/19.
 */
@Entity(tableName = "sentiment")
data class SentimentEntity(
    @PrimaryKey val tweetId: String,
    val score: Float,
    val magnitude: Float
)