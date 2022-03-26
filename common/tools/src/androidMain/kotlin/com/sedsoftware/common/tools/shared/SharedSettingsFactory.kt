package com.sedsoftware.common.tools.shared

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings

@Suppress("FunctionName")
fun SharedSettings(context: Context): Settings {
    val delegate: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    return AndroidSettings(delegate)
}
