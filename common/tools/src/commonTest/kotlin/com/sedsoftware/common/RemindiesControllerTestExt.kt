package com.sedsoftware.common

import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.tools.RemindiesAlarmManager

interface ShotChecker {
    fun isScheduled(shot: NextShot)
}

class RemindiesAlarmManagerTest : RemindiesAlarmManager, ShotChecker {

    private val shots: MutableSet<NextShot> = mutableSetOf()

    override fun schedule(shot: NextShot) {
        shots.add(shot)
    }

    override fun cancel(shot: NextShot) {
        shots.remove(shot)
    }

    override fun isScheduled(shot: NextShot) {
        shots.contains(shot)
    }
}
