package com.sedsoftware.common.component.create

import com.arkivanov.decompose.value.Value
import com.sedsoftware.common.domain.type.RemindiePeriod
import kotlinx.datetime.LocalDateTime

interface RemindiesCreate {

    val models: Value<Model>

    fun onTitleTextChanged(text: String)

    fun onDescriptionTextChanged(text: String)

    fun onTargetSelected(target: LocalDateTime)

    fun onPeriodSelected(period: RemindiePeriod)

    fun onPeriodValueSelected(value: Int)

    fun onSaveRequested()

    data class Model(
        val title: String,
        val description: String,
        val target: LocalDateTime,
        val period: RemindiePeriod,
        val each: Int,
        val saveEnabled: Boolean
    )

    sealed class Output {
        object RemindieCreated : Output()
    }
}
