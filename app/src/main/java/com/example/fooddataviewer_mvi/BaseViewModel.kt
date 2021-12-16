package com.example.fooddataviewer_mvi

import androidx.lifecycle.ViewModel
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.Update
import com.spotify.mobius.android.AndroidLogger
import com.spotify.mobius.functions.Producer
import com.spotify.mobius.rx2.RxEventSources
import com.spotify.mobius.rx2.RxMobius
import com.spotify.mobius.rx2.SchedulerWorkRunner
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.atomic.AtomicBoolean

interface ViewModelInt<M, E> : ObservableTransformer<E, M> {

    fun init(event: E): ViewModelInt<M, E>
}

abstract class BaseViewModel<M, E, F>(
    tag: String,
    update: Update<M, E, F>,
    initialModel: M,
    effectHandler: ObservableTransformer<F, E>,
    vararg eventSources: ObservableSource<E>
) : ViewModel(), ViewModelInt<M, E> {

    private val loop: MobiusLoop<M, E, F>

    private val initialized = AtomicBoolean(false)

    init {
        var builder = RxMobius
            .loop(update, effectHandler)
            .eventRunner(Producer { SchedulerWorkRunner(AndroidSchedulers.mainThread()) })
            .effectRunner(Producer { SchedulerWorkRunner(AndroidSchedulers.mainThread()) })
            .logger(AndroidLogger(tag))
        if (eventSources.isNotEmpty()) {
            builder = builder.eventSource(RxEventSources.fromObservables(*eventSources))
        }
        loop = builder.startFrom(initialModel)
    }

    final override fun init(event: E): ViewModelInt<M, E> {
        if (!initialized.getAndSet(true)) {
            loop.dispatchEvent(event)
        }
        return this
    }

    final override fun apply(upstream: Observable<E>): ObservableSource<M> =
        Observable.create { emitter ->
            val eventDisposable = upstream.subscribe { event -> loop.dispatchEvent(event) }
            val modelDisposable = loop.observe { model -> emitter.onNext(model) }
            emitter.setCancellable {
                eventDisposable.dispose()
                modelDisposable.dispose()
            }
        }

    final override fun onCleared() {
        super.onCleared()
        loop.dispose()
    }
}