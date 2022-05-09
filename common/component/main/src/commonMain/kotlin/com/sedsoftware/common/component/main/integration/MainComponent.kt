package com.sedsoftware.common.component.main.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.asValue
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.invoke
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.main.RemindiesMain
import com.sedsoftware.common.component.main.RemindiesMain.Model
import com.sedsoftware.common.component.main.RemindiesMain.Output
import com.sedsoftware.common.component.main.infrastructure.RemindiesMainController
import com.sedsoftware.common.component.main.integration.Mapper.stateToModel
import com.sedsoftware.common.component.main.store.MainStore.Intent
import com.sedsoftware.common.component.main.store.MainStoreFactory
import com.sedsoftware.common.database.RemindiesSharedDatabase
import com.sedsoftware.common.domain.entity.Remindie
import com.sedsoftware.common.tools.RemindiesAlarmManager
import com.sedsoftware.common.tools.RemindiesSharedSettings
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
class MainComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val output: Consumer<Output>,
    database: RemindiesSharedDatabase,
    manager: RemindiesAlarmManager,
    settings: RemindiesSharedSettings
) : RemindiesMain, ComponentContext by componentContext {

    private val baseController: RemindiesController =
        RemindiesController(database, manager, settings)

    private val store =
        instanceKeeper.getStore {
            MainStoreFactory(
                storeFactory = storeFactory,
                controller = RemindiesMainController(baseController)
            ).create()
        }

    override val models: Value<Model> = store.asValue().map(stateToModel)

    override fun onAddItemRequested() {
        output(Output.RequestRemindieCreator)
    }

    override fun onItemDeleted(item: Remindie) {
        store.accept(Intent.DeleteRemindie(item))
    }
}
