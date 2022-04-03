package com.sedsoftware.common.domain.entity

import com.sedsoftware.common.domain.type.RemindiePeriod
import com.sedsoftware.common.domain.type.RemindieType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals

class ShotExtTest {

    @Test
    fun updateTimeZone_test() {
        val currentTimeZone = TimeZone.of("GMT+2")
        val newTimeZone = TimeZone.of("GMT+3")

        val today = LocalDateTime(2020, 11, 7, 22, 13)
        val shot = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie = Remindie(
            createdTimestamp = 1234,
            createdDate = today,
            targetTime = shot,
            creationTimeZone = currentTimeZone,
            title = "Daily at 18:33, next shot at 8/11/2020 at 18:33",
            description = "Do something cool",
            type = RemindieType.CAFE,
            period = RemindiePeriod.DAILY,
            each = 1
        )

        val initialShot = remindie.toNextShot(today = today)
        val newTimeZoneShot = remindie.toNextShot(today = today).updateTimeZone(newTimeZone)

        // initial
        assertEquals(initialShot.target.hour, 18)
        assertEquals(initialShot.target.minute, 33)

        // updated
        assertEquals(newTimeZoneShot.target.hour, 19)
        assertEquals(newTimeZoneShot.target.minute, 33)
    }
}
