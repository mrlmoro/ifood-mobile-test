package br.com.murilomoro.tweetlikes.data.local.sentiment

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Single

/**
 * Created by Murilo Moro on 28/01/19.
 */
@Dao
interface SentimentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(sentimentEntity: SentimentEntity)

    @Query("SELECT * FROM sentiment WHERE tweetId = :tweetId LIMIT 1")
    fun find(tweetId: String): Single<SentimentEntity>

}