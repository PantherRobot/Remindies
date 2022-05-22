package com.sedsoftware.common.component.create.integration

import com.badoo.reaktive.completable.Completable
import com.sedsoftware.common.domain.type.RemindiePeriod
import kotlinx.datetime.LocalDateTime

interface CreationController {
    fun add(title: String, description: String, target: LocalDateTime, period: RemindiePeriod, each: Int): Completable
}
