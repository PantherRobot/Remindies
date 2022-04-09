package com.sedsoftware.common.domain.entity

import com.sedsoftware.common.domain.type.RemindiePeriod
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plusPeriod
import kotlinx.datetime.toLocalDateTime

fun Remindie.toNextShot(today: LocalDateTime = Clock.System.now().toLocalDateTime(creationTimeZone)): NextShot {

    // Not fired yet
    if (targetTime > today) {
        return NextShot(remindie = this, target = targetTime, isFired = false)
    }

    // Already fired for oneshot
    if (period == RemindiePeriod.NONE) {
        return NextShot(remindie = this, target = targetTime, isFired = true)
    }

    var closest: LocalDateTime = targetTime

    while (closest < today) {
        closest = closest.plusPeriod(period, each, creationTimeZone)
    }

    return NextShot(remindie = this, target = closest, isFired = false)
}

fun Remindie.getShots(from: LocalDateTime, to: LocalDateTime, today: LocalDateTime): List<NextShot> {
    val result = mutableListOf<NextShot>()

    if (targetTime > to) {
        return result
    }

    var temp = targetTime

    while (temp <= to) {
        if (temp in from..to) {
            result.add(
                NextShot(
                    remindie = this,
                    target = temp,
                    isFired = temp < today
                )
            )
        }

        temp = temp.plusPeriod(period, each, creationTimeZone)
    }

    return result
}
