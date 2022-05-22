package com.sedsoftware.common.component.create.store

import com.arkivanov.mvikotlin.core.store.Store
import com.sedsoftware.common.component.create.store.CreationStore.Intent
import com.sedsoftware.common.component.create.store.CreationStore.Label
import com.sedsoftware.common.component.create.store.CreationStore.State
import com.sedsoftware.common.domain.type.RemindiePeriod
import kotlinx.datetime.LocalDateTime

internal interface CreationStore : Store<Intent, State, Label> {

    sealed class Intent {
        data class TitleInputAvailable(val txt: String) : Intent()
        data class DescriptionInputAvailable(val txt: String) : Intent()
        data class TargetDateSelected(val date: LocalDateTime) : Intent()
        data class PeriodSelected(val period: RemindiePeriod) : Intent()
        data class PeriodValueSelected(val value: Int) : Intent()
        object Save : Intent()
    }

    data class State(
        val title: String = "",
        val description: String = "",
        val target: LocalDateTime? = null,
        val period: RemindiePeriod = RemindiePeriod.NONE,
        val each: Int = 0,
        val saveEnabled: Boolean = false
    )

    sealed class Label {
        data class ErrorCaught(val exception: Exception) : Label()
    }
}
