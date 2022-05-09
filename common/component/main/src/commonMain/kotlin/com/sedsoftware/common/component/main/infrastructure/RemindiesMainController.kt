package com.sedsoftware.common.component.main.infrastructure

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.observable.Observable
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.main.integration.MainController
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.entity.Remindie
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
class RemindiesMainController(
    private val controller: RemindiesController
) : MainController {

    override val updates: Observable<List<NextShot>> =
        controller.getShotsForToday()

    override fun delete(remindie: Remindie): Completable =
        controller.remove(remindie)
}
