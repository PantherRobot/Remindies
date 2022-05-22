package com.sedsoftware.common.component.main.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.reaktiveExecutorFactory
import com.badoo.reaktive.completable.doOnAfterError
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.observable.doOnAfterError
import com.badoo.reaktive.observable.doOnAfterSubscribe
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.sedsoftware.common.component.main.integration.MainController
import com.sedsoftware.common.component.main.model.MainPageStatus
import com.sedsoftware.common.component.main.store.MainStore.Intent
import com.sedsoftware.common.component.main.store.MainStore.Label
import com.sedsoftware.common.component.main.store.MainStore.State
import com.sedsoftware.common.domain.entity.NextShot
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
internal class MainStoreFactory(
    private val storeFactory: StoreFactory,
    private val controller: MainController
) {

    fun create(): MainStore =
        object : MainStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "RemindiesMainStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper<Action>(Action.ObserveTodayShots),
            executorFactory = reaktiveExecutorFactory {

                onAction<Action.ObserveTodayShots> {
                    controller.updates
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnAfterSubscribe { dispatch(Msg.ShotsLoadingStarted) }
                        .doOnAfterError {
                            dispatch(Msg.ShotsLoadingFailed(it))
                            publish(Label.ErrorCaught(it))
                        }
                        .subscribeScoped { dispatch(Msg.ShotsLoadingCompleted(it)) }
                }

                onIntent<Intent.DeleteRemindie> { intent ->
                    controller.delete(intent.remindie)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnAfterError { publish(Label.ErrorCaught(it)) }
                        .subscribeScoped()
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.ShotsLoadingStarted -> copy(
                        status = MainPageStatus.LOADING
                    )
                    is Msg.ShotsLoadingCompleted -> copy(
                        shots = msg.shots,
                        status = if (msg.shots.isNotEmpty()) {
                            MainPageStatus.CONTENT
                        } else {
                            MainPageStatus.EMPTY
                        }
                    )
                    is Msg.ShotsLoadingFailed -> copy(
                        status = MainPageStatus.CONTENT
                    )
                }
            }
        ) {}

    private sealed interface Action {
        object ObserveTodayShots : Action
    }

    private sealed interface Msg {
        object ShotsLoadingStarted : Msg
        data class ShotsLoadingCompleted(val shots: List<NextShot>) : Msg
        data class ShotsLoadingFailed(val exception: Throwable) : Msg
    }
}
