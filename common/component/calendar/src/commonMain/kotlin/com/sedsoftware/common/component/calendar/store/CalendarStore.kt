package com.sedsoftware.common.component.calendar.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.common.component.calendar.store.CalendarStore.Intent
import com.sedsoftware.common.component.calendar.store.CalendarStore.Label
import com.sedsoftware.common.component.calendar.store.CalendarStore.State
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.type.RemindieCalendarMode
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface CalendarStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class NewDateAvailable(val date: LocalDateTime) : Intent()
        data class NewModeAvailable(val mode: RemindieCalendarMode) : Intent()
        object ShowForCurrentSetup : Intent()
    }

    data class State(
        val date: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
        val mode: RemindieCalendarMode = RemindieCalendarMode.WEEK,
        val schedule: List<NextShot> = emptyList()
    )

    sealed class Label {
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
