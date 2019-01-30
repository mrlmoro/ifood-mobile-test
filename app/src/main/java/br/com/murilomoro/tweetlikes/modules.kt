package br.com.murilomoro.tweetlikes

import android.arch.persistence.room.Room
import android.content.Context.MODE_PRIVATE
import br.com.murilomoro.tweetlikes.data.local.AppDatabase
import br.com.murilomoro.tweetlikes.data.local.Preferences
import br.com.murilomoro.tweetlikes.data.local.PreferencesImpl
import br.com.murilomoro.tweetlikes.data.remote.GoogleNaturalLanguageApi
import br.com.murilomoro.tweetlikes.data.remote.GoogleNaturalLanguageApiImpl
import br.com.murilomoro.tweetlikes.data.remote.TwitterService
import br.com.murilomoro.tweetlikes.data.repository.TwitterRepository
import br.com.murilomoro.tweetlikes.data.repository.TwitterRepositoryImpl
import br.com.murilomoro.tweetlikes.domain.TwitterUseCase
import br.com.murilomoro.tweetlikes.domain.TwitterUseCaseImpl
import br.com.murilomoro.tweetlikes.presentation.TweetSentimentDialogFragment
import br.com.murilomoro.tweetlikes.presentation.TwitterTweetsAdapter
import br.com.murilomoro.tweetlikes.presentation.TwitterTweetsViewModel
import br.com.murilomoro.tweetlikes.utils.Logger
import br.com.murilomoro.tweetlikes.utils.LoggerImpl
import com.google.gson.Gson
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.network.OkHttpClientHelper
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.experimental.builder.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Murilo Moro on 26/01/19.
 */

val appModule = module {

    single { Gson() }

    single<Logger> { LoggerImpl() }
}

val dataModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            androidContext().getString(R.string.database_name)
        ).allowMainThreadQueries().build()
    }

    single { get<AppDatabase>().sentimentDao() }

    single<TwitterService> {
        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClientHelper.getOkHttpClient(
                    TwitterCore.getInstance().guestSessionProvider
                )
            )
            .baseUrl(TwitterApi().baseHostUrl)
            .build()
            .create(TwitterService::class.java)
    }

    single<Preferences> {
        PreferencesImpl(
            androidContext().getSharedPreferences(
                androidContext().getString(R.string.preference_name),
                MODE_PRIVATE
            ),
            get()
        )
    }

    single<TwitterRepository> { TwitterRepositoryImpl(get(), get(), get(), get(), get()) }

    single<GoogleNaturalLanguageApi> { GoogleNaturalLanguageApiImpl(get(), get()) }

}

val domainModule = module {

    single<TwitterUseCase> { TwitterUseCaseImpl(get()) }
}

val presentationModule = module {

    viewModel<TwitterTweetsViewModel>()

    factory { TwitterTweetsAdapter(get()) }

    factory { TweetSentimentDialogFragment() }
}

val modules = listOf(
    appModule,
    dataModule,
    domainModule,
    presentationModule
)