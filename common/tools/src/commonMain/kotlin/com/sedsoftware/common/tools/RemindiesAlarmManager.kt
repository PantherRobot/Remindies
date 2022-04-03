package com.sedsoftware.common.tools

import com.sedsoftware.common.domain.entity.NextShot

interface RemindiesAlarmManager {
    fun schedule(shot: NextShot)
    fun cancel(shot: NextShot)
}
