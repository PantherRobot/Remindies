package com.sedsoftware.common.component.create.infrastructure

import com.badoo.reaktive.completable.Completable
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.create.integration.CreationController
import com.sedsoftware.common.domain.type.RemindiePeriod
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
class RemindiesCreationController(
    private val controller: RemindiesController
) : CreationController {

    override fun add(title: String, description: String, target: LocalDateTime, period: RemindiePeriod, each: Int): Completable =
        controller.add(title, description, target, period, each)
}
