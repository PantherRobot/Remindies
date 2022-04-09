package com.sedsoftware.common

import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.tools.RemindiesAlarmManager

interface ShotChecker {
    val nearestShot: NextShot?
}

class RemindiesAlarmManagerTest : RemindiesAlarmManager, ShotChecker {

    private val shots: MutableSet<NextShot> = mutableSetOf()

    override val nearestShot: NextShot?
        get() = shots.minByOrNull { it.target }

    override fun schedule(shot: NextShot) {
        shots.add(shot)
    }

    override fun cancel(shot: NextShot) {
        shots.remove(shot)
    }
}
