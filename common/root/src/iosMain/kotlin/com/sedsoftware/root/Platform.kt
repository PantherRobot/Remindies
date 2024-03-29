package com.sedsoftware.root

import platform.UIKit.UIDevice

@Suppress("MemberNameEqualsClassName")
actual class Platform actual constructor() {
    actual val platform: String = UIDevice.currentDevice.systemName() + " - " + UIDevice.currentDevice.systemVersion
}
