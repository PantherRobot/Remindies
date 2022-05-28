package com.sedsoftware.common.component.calendar.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.asValue
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.invoke
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.calendar.RemindiesCalendar
import com.sedsoftware.common.component.calendar.RemindiesCalendar.Model
import com.sedsoftware.common.component.calendar.RemindiesCalendar.Output
import com.sedsoftware.common.component.calendar.infrastructure.RemindiesCalendarController
import com.sedsoftware.common.component.calendar.integration.Mapper.stateToModel
import com.sedsoftware.common.component.calendar.store.CalendarStore
import com.sedsoftware.common.component.calendar.store.CalendarStore.Label
import com.sedsoftware.common.component.calendar.store.CalendarStoreFactory
import com.sedsoftware.common.database.RemindiesSharedDatabase
import com.sedsoftware.common.domain.type.RemindieCalendarMode
import com.sedsoftware.common.tools.RemindiesAlarmManager
import com.sedsoftware.common.tools.RemindiesSharedSettings
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
class CalendarComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val output: Consumer<Output>,
    database: RemindiesSharedDatabase,
    manager: RemindiesAlarmManager,
    settings: RemindiesSharedSettings
) : RemindiesCalendar, ComponentContext by componentContext {

    private val baseController: RemindiesController =
        RemindiesController(database, manager, settings)

    private val store =
        instanceKeeper.getStore {
            CalendarStoreFactory(
                storeFactory = storeFactory,
                controller = RemindiesCalendarController(baseController)
            ).create()
        }

    init {
        val disposable = store.labels
            .subscribeOn(mainScheduler)
            .subscribe { label ->
                when (label) {
                    is Label.ErrorCaught -> output(Output.ErrorCaught(label.throwable))
                }
            }

        lifecycle.doOnDestroy {
            disposable.dispose()
        }
    }

    override val models: Value<Model> = store.asValue().map(stateToModel)

    override fun onNewModeSelected(mode: RemindieCalendarMode) {
        store.accept(CalendarStore.Intent.NewModeAvailable(mode))
        store.accept(CalendarStore.Intent.ShowForCurrentSetup)
    }

    override fun onNewDateSelected(date: LocalDateTime) {
        store.accept(CalendarStore.Intent.NewDateAvailable(date))
        store.accept(CalendarStore.Intent.ShowForCurrentSetup)
    }
}
