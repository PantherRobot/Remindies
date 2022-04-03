package com.sedsoftware.common

import com.badoo.reaktive.scheduler.trampolineScheduler
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MockSettings
import com.sedsoftware.common.database.RemindiesSharedDatabase
import com.sedsoftware.common.database.TestRemindiesSharedDatabase
import com.sedsoftware.common.tools.RemindiesSharedSettings
import kotlin.test.BeforeTest
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSettingsApi
class RemindiesControllerTest {

    private lateinit var database: RemindiesSharedDatabase
    private lateinit var settings: RemindiesSharedSettings
    private lateinit var manager: RemindiesAlarmManagerTest
    private lateinit var controller: RemindiesController

    @BeforeTest
    fun setUp() {
        database = TestRemindiesSharedDatabase(trampolineScheduler)
        settings = RemindiesSharedSettings(MockSettings())
        manager = RemindiesAlarmManagerTest()
        controller = RemindiesController(database, manager, settings)
    }

    // TODO
}
