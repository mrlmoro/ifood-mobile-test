package br.com.murilomoro.tweetlikes.unit

import android.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.mockito.MockitoAnnotations

/**
 * Created by Murilo Moro on 29/01/19.
 */
abstract class BaseUnitTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    abstract fun `Create system under test`()

    @Before
    fun `Setup Test`() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

        `Create system under test`()
    }
}