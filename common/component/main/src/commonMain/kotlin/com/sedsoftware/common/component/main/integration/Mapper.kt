package com.sedsoftware.common.component.main.integration

import com.sedsoftware.common.component.main.RemindiesMain.Model
import com.sedsoftware.common.component.main.store.MainStore.State

internal object Mapper {
    val stateToModel: (State) -> Model = {
        Model(
            shots = it.shots,
            status = it.status
        )
    }
}
