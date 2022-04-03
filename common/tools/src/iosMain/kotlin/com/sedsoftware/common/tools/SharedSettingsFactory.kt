package com.sedsoftware.common.tools

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults

@Suppress("FunctionName")
fun SharedSettings(): Settings {
    val delegate: NSUserDefaults = NSUserDefaults.standardUserDefaults()
    return AppleSettings(delegate)
}
