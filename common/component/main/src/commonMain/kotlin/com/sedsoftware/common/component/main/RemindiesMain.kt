package com.sedsoftware.common.component.main

import com.arkivanov.decompose.value.Value
import com.sedsoftware.common.component.main.model.MainPageStatus
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.entity.Remindie

interface RemindiesMain {

    val models: Value<Model>

    fun onAddItemRequested()

    fun onItemDeleted(item: Remindie)

    data class Model(
        val shots: List<NextShot>,
        val status: MainPageStatus
    )

    sealed class Output {
        object RequestRemindieCreator : Output()
    }
}
