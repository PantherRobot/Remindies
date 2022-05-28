package com.sedsoftware.common.component.calendar.integration

import com.badoo.reaktive.observable.Observable
import com.sedsoftware.common.domain.entity.NextShot
import kotlinx.datetime.LocalDateTime

interface CalendarController {
    fun getForWeek(date: LocalDateTime): Observable<List<NextShot>>
    fun getForMonth(date: LocalDateTime): Observable<List<NextShot>>
    fun getForYear(date: LocalDateTime): Observable<List<NextShot>>
}
