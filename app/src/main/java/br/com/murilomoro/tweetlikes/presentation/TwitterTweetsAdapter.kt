package br.com.murilomoro.tweetlikes.presentation

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.murilomoro.tweetlikes.R
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.CompactTweetView
import kotlinx.android.synthetic.main.item_tweet.view.*

/**
 * Created by Murilo Moro on 26/01/19.
 */
class TwitterTweetsAdapter(
    private val context: Context
) : RecyclerView.Adapter<TwitterTweetsAdapter.ViewHolder>() {

    private val tweets = mutableListOf<Tweet>()

    var listener: OnTweetClickListener? = null

    interface OnTweetClickListener {
        fun onTweetClicked(tweet: Tweet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_tweet, parent, false)
        return ViewHolder(context, view)
    }

    override fun getItemCount(): Int = tweets.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tweets[position], listener)
    }

    fun notifyChanged(tweets: List<Tweet>) {
        this.tweets.clear()
        this.tweets.addAll(tweets)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val context: Context,
        view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(tweet: Tweet, listener: OnTweetClickListener? = null) {
            itemView.cv_tweet.removeAllViews()
            itemView.cv_tweet.addView(CompactTweetView(context, tweet))

            itemView.cv_tweet.setOnClickListener {
                listener?.onTweetClicked(tweet)
            }
        }
    }
}