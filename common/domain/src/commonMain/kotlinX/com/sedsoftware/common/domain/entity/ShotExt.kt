package com.sedsoftware.common.domain.entity

import kotlinx.datetime.TimeZone
import kotlinx.datetime.moveToZone
import kotlinx.datetime.toInstant

fun NextShot.updateTimeZone(current: TimeZone = TimeZone.currentSystemDefault()): NextShot {
    if (remindie.creationTimeZone == current) {
        return this
    }

    return copy(target = this.target.moveToZone(remindie.creationTimeZone, current))
}

fun NextShot.toScheduledTime(): Long {
    return this.target.toInstant(TimeZone.currentSystemDefault()).epochSeconds
}
