package br.com.murilomoro.tweetlikes.ui.base

import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Murilo Moro on 26/01/19.
 */
abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    protected fun <T> subscribe(
        observable: Single<T>,
        success: ((T) -> Unit)? = null,
        error: ((Throwable) -> Unit)? = null
    ): Disposable {
        val disposable = observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(success, error)

        compositeDisposable.add(disposable)

        return disposable
    }

    protected fun dispose(disposable: Disposable?) {
        disposable?.let { compositeDisposable.remove(it) }
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}