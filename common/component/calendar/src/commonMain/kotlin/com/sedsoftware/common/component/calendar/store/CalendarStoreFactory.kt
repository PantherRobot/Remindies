package com.sedsoftware.common.component.calendar.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.reaktiveExecutorFactory
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.doOnAfterError
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.sedsoftware.common.component.calendar.integration.CalendarController
import com.sedsoftware.common.component.calendar.store.CalendarStore.Intent
import com.sedsoftware.common.component.calendar.store.CalendarStore.Label
import com.sedsoftware.common.component.calendar.store.CalendarStore.State
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.type.RemindieCalendarMode
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
internal class CalendarStoreFactory(
    private val storeFactory: StoreFactory,
    private val controller: CalendarController
) {

    fun create(): CalendarStore =
        object : CalendarStore, Store<Intent, State, Label> by storeFactory.create<Intent, Action, Msg, State, Label>(
            name = "RemindiesCalendarStore",
            initialState = State(),
            bootstrapper = SimpleBootstrapper<Action>(Action.ShowCurrentCalendar),
            executorFactory = reaktiveExecutorFactory {
                onIntent<Intent.NewDateAvailable> { dispatch(Msg.DateChosen(it.date)) }

                onIntent<Intent.NewModeAvailable> { dispatch(Msg.ModeChosen(it.mode)) }

                onIntent<Intent.ShowForCurrentSetup> {
                    getForCurrentState(state)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnAfterError { publish(Label.ErrorCaught(it)) }
                        .subscribeScoped { dispatch(Msg.ShotsAvailable(it)) }
                }

                onAction<Action.ShowCurrentCalendar> {
                    getForCurrentState(state)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .doOnAfterError { publish(Label.ErrorCaught(it)) }
                        .subscribeScoped { dispatch(Msg.ShotsAvailable(it)) }
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.DateChosen -> copy(
                        date = msg.date
                    )
                    is Msg.ModeChosen -> copy(
                        mode = msg.mode
                    )
                    is Msg.ShotsAvailable -> copy(
                        schedule = msg.shots
                    )
                }
            }
        ) {}

    private fun getForCurrentState(state: State): Observable<List<NextShot>> =
        when (state.mode) {
            RemindieCalendarMode.WEEK -> controller.getForWeek(state.date)
            RemindieCalendarMode.MONTH -> controller.getForMonth(state.date)
            RemindieCalendarMode.YEAR -> controller.getForYear(state.date)
        }

    private sealed interface Action {
        object ShowCurrentCalendar : Action
    }

    private sealed interface Msg {
        data class DateChosen(val date: LocalDateTime) : Msg
        data class ModeChosen(val mode: RemindieCalendarMode) : Msg
        data class ShotsAvailable(val shots: List<NextShot>) : Msg
    }
}
