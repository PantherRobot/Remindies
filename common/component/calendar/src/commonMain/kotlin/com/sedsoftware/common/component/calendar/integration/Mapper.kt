package com.sedsoftware.common.component.calendar.integration

import com.sedsoftware.common.component.calendar.RemindiesCalendar.Model
import com.sedsoftware.common.component.calendar.store.CalendarStore.State

internal object Mapper {
    val stateToModel: (State) -> Model = {
        Model(
            date = it.date,
            mode = it.mode,
            schedule = it.schedule
        )
    }
}
