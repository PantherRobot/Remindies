package com.sedsoftware.common.tools

import com.sedsoftware.common.domain.entity.NextShot

interface RemindiesScheduler {
    fun schedule(nextShot: NextShot)
    fun cancel(nextShot: NextShot)
}
