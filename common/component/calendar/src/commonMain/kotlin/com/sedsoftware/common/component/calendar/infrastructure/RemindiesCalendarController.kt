package com.sedsoftware.common.component.calendar.infrastructure

import com.badoo.reaktive.observable.Observable
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.calendar.integration.CalendarController
import com.sedsoftware.common.domain.entity.NextShot
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
internal class RemindiesCalendarController(
    private val controller: RemindiesController
) : CalendarController {

    override fun getForWeek(date: LocalDateTime): Observable<List<NextShot>> =
        controller.getShotsForWeek(date)

    override fun getForMonth(date: LocalDateTime): Observable<List<NextShot>> =
        controller.getShotsForMonth(date)

    override fun getForYear(date: LocalDateTime): Observable<List<NextShot>> =
        controller.getShotsForYear(date)
}
