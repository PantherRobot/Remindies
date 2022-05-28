package com.sedsoftware.common.component.create.integration

import com.sedsoftware.common.component.create.RemindiesCreate.Model
import com.sedsoftware.common.component.create.store.CreationStore.State

internal object Mapper {
    val stateToModel: (State) -> Model = {
        Model(
            title = it.title,
            description = it.description,
            target = it.target,
            period = it.period,
            each = it.each,
            saveEnabled = it.saveEnabled
        )
    }
}
