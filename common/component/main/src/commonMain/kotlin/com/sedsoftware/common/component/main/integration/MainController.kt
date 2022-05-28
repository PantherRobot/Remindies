package com.sedsoftware.common.component.main.integration

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.observable.Observable
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.entity.Remindie

internal interface MainController {
    val updates: Observable<List<NextShot>>

    fun delete(remindie: Remindie): Completable
}
