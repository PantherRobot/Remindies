package com.sedsoftware.common.component.create.store

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.reaktive.reaktiveExecutorFactory
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.badoo.reaktive.scheduler.mainScheduler
import com.sedsoftware.common.component.create.integration.CreationController
import com.sedsoftware.common.component.create.store.CreationStore.Intent
import com.sedsoftware.common.component.create.store.CreationStore.Label
import com.sedsoftware.common.component.create.store.CreationStore.State
import com.sedsoftware.common.domain.type.RemindiePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.available
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
internal class CreationStoreFactory(
    private val storeFactory: StoreFactory,
    private val controller: CreationController
) {

    fun create(): CreationStore =
        object : CreationStore, Store<Intent, State, Label> by storeFactory.create<Intent, Nothing, Msg, State, Label>(
            name = "RemindiesCreationStore",
            initialState = State(),
            executorFactory = reaktiveExecutorFactory {
                onIntent<Intent.TitleInputAvailable> { dispatch(Msg.TitleTextEntered(it.txt)) }

                onIntent<Intent.DescriptionInputAvailable> { dispatch(Msg.DescriptionTextEntered(it.txt)) }

                onIntent<Intent.TargetDateSelected> { dispatch(Msg.TargetDateSelected(it.date)) }

                onIntent<Intent.PeriodSelected> { dispatch(Msg.PeriodSelected(it.period)) }

                onIntent<Intent.PeriodValueSelected> { dispatch(Msg.PeriodEachSelected(it.value)) }

                onIntent<Intent.Save> {
                    controller.add(state.title, state.description, state.target, state.period, state.each)
                        .subscribeOn(ioScheduler)
                        .observeOn(mainScheduler)
                        .subscribeScoped()
                }
            },
            reducer = { msg ->
                when (msg) {
                    is Msg.TitleTextEntered -> copy(
                        title = msg.text,
                        saveEnabled = msg.text.isNotEmpty() && target.available()
                    )
                    is Msg.DescriptionTextEntered -> copy(
                        description = msg.text
                    )
                    is Msg.TargetDateSelected -> copy(
                        target = msg.date,
                        saveEnabled = title.isNotEmpty() && msg.date.available()
                    )
                    is Msg.PeriodSelected -> copy(
                        period = msg.period,
                        each = if (period == RemindiePeriod.NONE) 0 else each,
                    )
                    is Msg.PeriodEachSelected -> copy(
                        each = msg.each
                    )
                }
            }
        ) {}

    private sealed interface Msg {
        data class TitleTextEntered(val text: String) : Msg
        data class DescriptionTextEntered(val text: String) : Msg
        data class TargetDateSelected(val date: LocalDateTime) : Msg
        data class PeriodSelected(val period: RemindiePeriod) : Msg
        data class PeriodEachSelected(val each: Int) : Msg
    }

}
