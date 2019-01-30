package br.com.murilomoro.tweetlikes.presentation

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.MenuItem
import br.com.murilomoro.tweetlikes.R
import br.com.murilomoro.tweetlikes.databinding.ActivityTwitterBinding
import br.com.murilomoro.tweetlikes.ui.base.BaseActivity
import br.com.murilomoro.tweetlikes.utils.*
import com.twitter.sdk.android.core.models.Tweet
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit


class TwitterTweetsActivity : BaseActivity<ActivityTwitterBinding>(),
    TwitterTweetsAdapter.OnTweetClickListener,
    TweetSentimentDialogFragment.Listener {

    val viewModel by viewModel<TwitterTweetsViewModel>()

    private val tweetsAdapter by inject<TwitterTweetsAdapter>()

    private var tweetSentimentFragment: TweetSentimentDialogFragment? = null

    private lateinit var menuSearch: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var searchViewDisposable: Disposable

    override fun getLayoutRes() = R.layout.activity_twitter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.vm = viewModel

        initToolbarMenu()

        initRecyclerView()

        initTweetSentimentDialog()

        observeData()

        when {
            savedInstanceState == null -> {
                changeBackgroundColor(R.color.colorPrimary)
                viewModel.init()
            }
            isSentimentDialogAdded() -> viewModel.updateSentimentDialog()
        }
    }

    private fun initToolbarMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_twitter)

        menuSearch = binding.toolbar.menu.findItem(R.id.menu_search)
        menuSearch.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                compositeDisposable.remove(searchViewDisposable)
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                fillUpSearchViewQuery()
                observeSearchView()
                return true
            }
        })

        searchView = menuSearch.actionView as SearchView
        searchView.customStyle()
    }

    private fun observeSearchView() {
        searchViewDisposable = searchView.observe()
            .subscribeOn(Schedulers.io())
            .debounce(1000, TimeUnit.MILLISECONDS)
            .filter { it.isNotEmpty() }
            .distinctUntilChanged()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                viewModel.searchLastTweets(searchView.query.toString())
                menuSearch.collapseActionView()
            }
            .subscribe {
                viewModel.searchLastTweets(it)
            }

        compositeDisposable.add(searchViewDisposable)
    }

    private fun fillUpSearchViewQuery() {
        searchView.post {
            searchView.setQuery(
                viewModel.userName.get(),
                false
            )
        }
    }

    private fun initRecyclerView() {
        tweetsAdapter.listener = this
        binding.rvTweets.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvTweets.adapter = tweetsAdapter
    }

    private fun initTweetSentimentDialog() {
        tweetSentimentFragment = supportFragmentManager
            .findFragmentByTag(TweetSentimentDialogFragment.TAG) as TweetSentimentDialogFragment?

        if (tweetSentimentFragment == null) {
            tweetSentimentFragment = get()
        }

        tweetSentimentFragment?.listener = this
    }

    private fun observeData() {
        viewModel.tweets.observeNotNull(this) {
            tweetsAdapter.notifyChanged(it)
        }

        viewModel.tweetsError.observeNotNull(this) {
            showToast(getString(it.errorType.resId))
        }

        viewModel.tweetSentiment.observeNotNull(this) {
            if (tweetSentimentFragment?.isVisible == true) {
                tweetSentimentFragment?.bindViewModel()
                changeBackgroundColor(it.colorRes)
            }
        }

        viewModel.tweetSentimentError.observeNotNull(this) {
            tweetSentimentFragment?.dismissAllowingStateLoss()
            showToast(getString(it.errorType.resId))
        }
    }

    override fun onTweetClicked(tweet: Tweet) {
        tweetSentimentFragment?.show(supportFragmentManager)
        viewModel.analyzeTweetSentiment(tweet)
    }

    private fun changeBackgroundColor(colorRes: Int) {
        viewModel.currentBackgroundColor.set(colorRes)
        window?.changeNavAndStatusBarColor(getColorFromResource(colorRes))
    }

    override fun onTweetSentimentDialogClosed() {
        changeBackgroundColor(R.color.colorPrimary)
    }

    private fun isSentimentDialogAdded() =
        supportFragmentManager.findFragmentByTag(TweetSentimentDialogFragment.TAG) != null

}
