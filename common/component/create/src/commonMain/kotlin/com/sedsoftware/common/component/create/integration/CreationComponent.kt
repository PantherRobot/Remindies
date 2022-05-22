package com.sedsoftware.common.component.create.integration

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.store.asValue
import com.badoo.reaktive.base.Consumer
import com.sedsoftware.common.RemindiesController
import com.sedsoftware.common.component.create.RemindiesCreate
import com.sedsoftware.common.component.create.RemindiesCreate.Model
import com.sedsoftware.common.component.create.RemindiesCreate.Output
import com.sedsoftware.common.component.create.infrastructure.RemindiesCreationController
import com.sedsoftware.common.component.create.integration.Mapper.stateToModel
import com.sedsoftware.common.component.create.store.CreationStore.Intent
import com.sedsoftware.common.component.create.store.CreationStoreFactory
import com.sedsoftware.common.database.RemindiesSharedDatabase
import com.sedsoftware.common.domain.type.RemindiePeriod
import com.sedsoftware.common.tools.RemindiesAlarmManager
import com.sedsoftware.common.tools.RemindiesSharedSettings
import kotlinx.datetime.LocalDateTime
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Suppress("OPT_IN_USAGE")
class CreationComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val output: Consumer<Output>,
    database: RemindiesSharedDatabase,
    manager: RemindiesAlarmManager,
    settings: RemindiesSharedSettings
) : RemindiesCreate, ComponentContext by componentContext {

    private val baseController: RemindiesController =
        RemindiesController(database, manager, settings)

    private val store =
        instanceKeeper.getStore {
            CreationStoreFactory(
                storeFactory = storeFactory,
                controller = RemindiesCreationController(baseController)
            ).create()
        }

    override val models: Value<Model> = store.asValue().map(stateToModel)

    override fun onTitleTextChanged(text: String) {
        store.accept(Intent.TitleInputAvailable(text))
    }

    override fun onDescriptionTextChanged(text: String) {
        store.accept(Intent.DescriptionInputAvailable(text))
    }

    override fun onTargetSelected(target: LocalDateTime) {
        store.accept(Intent.TargetDateSelected(target))
    }

    override fun onPeriodSelected(period: RemindiePeriod) {
        store.accept(Intent.PeriodSelected(period))
    }

    override fun onPeriodValueSelected(value: Int) {
        store.accept(Intent.PeriodValueSelected(value))
    }

    override fun onSaveRequested() {
        store.accept(Intent.Save)
    }
}
