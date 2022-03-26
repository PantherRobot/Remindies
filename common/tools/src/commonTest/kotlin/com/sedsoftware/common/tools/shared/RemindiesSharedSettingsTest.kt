package com.sedsoftware.common.tools.shared

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MockSettings
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExperimentalSettingsApi
class RemindiesSharedSettingsTest {

    private val settings: RemindiesSharedSettings = RemindiesSharedSettings(MockSettings())

    @Test
    fun timeZoneDependent_test() {
        // Test default
        assertTrue { settings.timeZoneDependent }
        // Test changing
        settings.timeZoneDependent = false
        assertFalse { settings.timeZoneDependent }
        settings.timeZoneDependent = true
        assertTrue { settings.timeZoneDependent }
    }

    @Test
    fun startFromSunday_test() {
        // Test default
        assertFalse { settings.startFromSunday }
        // Test changing
        settings.startFromSunday = true
        assertTrue { settings.startFromSunday }
        settings.startFromSunday = false
        assertFalse { settings.startFromSunday }
    }
}
