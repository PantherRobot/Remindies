package com.sedsoftware.common.component.main.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.asValue
import com.arkivanov.mvikotlin.extensions.reaktive.labels
import com.badoo.reaktive.base.Consumer
import com.badoo.reaktive.base.invoke
import com.badoo.reaktive.observable.subscribe
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.mainScheduler
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.main.RemindiesMain
import com.sedsoftware.common.component.main.RemindiesMain.Model
import com.sedsoftware.common.component.main.RemindiesMain.Output
import com.sedsoftware.common.component.main.infrastructure.RemindiesMainController
import com.sedsoftware.common.component.main.integration.Mapper.stateToModel
import com.sedsoftware.common.component.main.store.MainStore.Intent
import com.sedsoftware.common.component.main.store.MainStore.Label
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

    init {
        val disposable = store.labels
            .subscribeOn(mainScheduler)
            .subscribe { label ->
                when (label) {
                    is Label.ErrorCaught -> output(Output.ErrorCaught(label.throwable))
                }
            }

        lifecycle.doOnDestroy {
            disposable.dispose()
        }
    }

    override val models: Value<Model> = store.asValue().map(stateToModel)

    override fun onAddItemRequested() {
        output(Output.CreatorRequested)
    }

    override fun onItemDeleted(item: Remindie) {
        store.accept(Intent.DeleteRemindie(item))
    }


}
