package br.com.murilomoro.tweetlikes.presentation

import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.murilomoro.tweetlikes.R
import br.com.murilomoro.tweetlikes.databinding.FragmentTweetSentimentBinding
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by Murilo Moro on 27/01/19.
 */
class TweetSentimentDialogFragment : AppCompatDialogFragment() {

    companion object {
        const val TAG = "TweetSentimentDialogFragment"
    }

    private val viewModel by sharedViewModel<TwitterTweetsViewModel>()

    private lateinit var binding: FragmentTweetSentimentBinding

    var listener: Listener? = null

    interface Listener {
        fun onTweetSentimentDialogClosed()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(context!!, R.style.BottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tweet_sentiment,
            container,
            false
        )

        bindViewModel()

        return binding.root
    }

    fun bindViewModel() {
        binding.vm = viewModel
    }

    override fun onDestroyView() {
        listener?.onTweetSentimentDialogClosed()
        super.onDestroyView()
    }

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }
}