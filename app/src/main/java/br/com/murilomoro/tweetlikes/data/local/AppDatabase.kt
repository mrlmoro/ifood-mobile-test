package br.com.murilomoro.tweetlikes.data.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import br.com.murilomoro.tweetlikes.data.local.sentiment.SentimentDao
import br.com.murilomoro.tweetlikes.data.local.sentiment.SentimentEntity

/**
 * Created by Murilo Moro on 28/01/19.
 */
@Database(entities = [SentimentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun sentimentDao(): SentimentDao
}