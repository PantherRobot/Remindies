package com.sedsoftware.common.tools

import com.sedsoftware.common.domain.entity.Shot

interface RemindiesScheduler {
    fun schedule(shot: Shot)
    fun cancel(shot: Shot)
}
