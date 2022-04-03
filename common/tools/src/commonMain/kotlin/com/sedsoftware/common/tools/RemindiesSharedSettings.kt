package com.sedsoftware.common.tools

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings

@ExperimentalSettingsApi
class RemindiesSharedSettings(
    private val settings: Settings
) {

    var timeZoneDependent: Boolean
        get() = settings.getValue(KEY_TIMEZONE_DEPENDENT, true)
        set(value) {
            settings.setValue(KEY_TIMEZONE_DEPENDENT, value)
        }

    var startFromSunday: Boolean
        get() = settings.getValue(KEY_START_FROM_SUNDAY, false)
        set(value) {
            settings.setValue(KEY_START_FROM_SUNDAY, value)
        }

    private fun Settings.setValue(key: String, value: Any) {
        when (value) {
            is String -> { this.putString(key, value) }
            is Int -> { this.putInt(key, value) }
            is Boolean -> { this.putBoolean(key, value) }
            is Float -> { this.putFloat(key, value) }
            is Long -> { this.putLong(key, value) }
            else -> throw UnsupportedOperationException("Not implemented yet")
        }
    }

    private inline fun <reified T : Any> Settings.getValue(key: String, defaultValue: T? = null): T =
        when (T::class) {
            String::class -> getString(key, defaultValue as? String ?: "") as T
            Int::class -> getInt(key, defaultValue as? Int ?: -1) as T
            Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T
            Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T
            Long::class -> getLong(key, defaultValue as? Long ?: -1L) as T
            else -> throw UnsupportedOperationException("Not implemented yet")
        }

    private companion object {
        const val KEY_TIMEZONE_DEPENDENT = "key_timezone_dependent"
        const val KEY_START_FROM_SUNDAY = "key_start_from_sunday"
    }
}
