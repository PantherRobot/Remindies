package com.sedsoftware.common.component.main.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.common.component.main.model.MainPageStatus
import com.sedsoftware.common.component.main.store.MainStore.Intent
import com.sedsoftware.common.component.main.store.MainStore.Label
import com.sedsoftware.common.component.main.store.MainStore.State
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.entity.Remindie

internal interface MainStore : Store<Intent, State, Label> {

    sealed class Intent {
        class DeleteRemindie(val remindie: Remindie) : Intent()
    }

    data class State(
        val shots: List<NextShot> = emptyList(),
        val status: MainPageStatus = MainPageStatus.LOADING
    )

    sealed class Label {
        data class ErrorCaught(val throwable: Throwable) : Label()
    }
}
