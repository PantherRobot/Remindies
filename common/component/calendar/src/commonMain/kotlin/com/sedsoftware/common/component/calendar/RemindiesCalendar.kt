package com.sedsoftware.common.component.calendar

import com.arkivanov.decompose.value.Value
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.type.RemindieCalendarMode
import kotlinx.datetime.LocalDateTime

interface RemindiesCalendar {

    val models: Value<Model>

    fun onNewModeSelected(mode: RemindieCalendarMode)

    fun onNewDateSelected(date: LocalDateTime)

    data class Model(
        val date: LocalDateTime,
        val mode: RemindieCalendarMode,
        val schedule: List<NextShot>,
    )

    sealed class Output {
        data class ErrorCaught(val throwable: Throwable) : Output()
    }
}
