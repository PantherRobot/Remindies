package com.sedsoftware.common.domain.entity

import com.sedsoftware.common.domain.type.RemindiePeriod
import com.sedsoftware.common.domain.type.RemindieType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.getDayEnd
import kotlinx.datetime.getDayStart
import kotlin.test.Test
import kotlin.test.assertEquals

class RemindieExtTest {

    @Test
    fun toNearestShot_test() {
        val timeZone = TimeZone.currentSystemDefault()

        // Oneshot - not fired
        val today1 = LocalDateTime(2020, 11, 7, 13, 13)
        val created1 = LocalDateTime(2020, 11, 7, 12, 13)
        val shot1 = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie1 = Remindie(
            createdTimestamp = 1,
            createdDate = created1,
            targetTime = shot1,
            creationTimeZone = timeZone,
            title = "Oneshot - not fired",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.NONE,
            each = 0
        )

        assertEquals(
            remindie1.toNextShot(today1),
            NextShot(
                remindie = remindie1,
                target = shot1,
                isFired = false
            )
        )

        // Oneshot - fired
        val today2 = LocalDateTime(2020, 11, 7, 22, 13)
        val created2 = LocalDateTime(2020, 11, 7, 12, 13)
        val shot2 = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie2 = Remindie(
            createdTimestamp = 2,
            createdDate = created2,
            targetTime = shot2,
            creationTimeZone = timeZone,
            title = "Oneshot - fired",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.NONE,
            each = 0
        )

        assertEquals(
            remindie2.toNextShot(today2),
            NextShot(
                remindie = remindie2,
                target = shot2,
                isFired = true
            )
        )

        // Periodical
        val today3 = LocalDateTime(2020, 11, 7, 22, 13)
        val created3 = LocalDateTime(2020, 11, 7, 12, 13)
        val shot3 = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie3 = Remindie(
            createdTimestamp = 3,
            createdDate = created3,
            targetTime = shot3,
            creationTimeZone = timeZone,
            title = "Oneshot - hourly 3",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.HOURLY,
            each = 3
        )

        assertEquals(
            remindie3.toNextShot(today3),
            NextShot(
                remindie = remindie3,
                target = LocalDateTime(2020, 11, 8, 0, 33),
                isFired = false
            )
        )

        val today4 = LocalDateTime(2020, 11, 7, 22, 13)
        val created4 = LocalDateTime(2020, 11, 7, 12, 13)
        val shot4 = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie4 = Remindie(
            createdTimestamp = 4,
            createdDate = created4,
            targetTime = shot4,
            creationTimeZone = timeZone,
            title = "Oneshot - daily 2",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.DAILY,
            each = 2
        )

        assertEquals(
            remindie4.toNextShot(today4),
            NextShot(
                remindie = remindie4,
                target = LocalDateTime(2020, 11, 9, 18, 33),
                isFired = false
            )
        )

        val today5 = LocalDateTime(2020, 11, 7, 22, 13)
        val created5 = LocalDateTime(2020, 11, 7, 12, 13)
        val shot5 = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie5 = Remindie(
            createdTimestamp = 5,
            createdDate = created5,
            targetTime = shot5,
            creationTimeZone = timeZone,
            title = "Oneshot - weekly 2",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.WEEKLY,
            each = 2
        )

        assertEquals(
            remindie5.toNextShot(today5),
            NextShot(
                remindie = remindie5,
                target = LocalDateTime(2020, 11, 21, 18, 33),
                isFired = false
            )
        )

        val today6 = LocalDateTime(2020, 11, 7, 22, 13)
        val created6 = LocalDateTime(2020, 11, 7, 12, 13)
        val shot6 = LocalDateTime(2020, 11, 7, 18, 33)

        val remindie6 = Remindie(
            createdTimestamp = 6,
            createdDate = created6,
            targetTime = shot6,
            creationTimeZone = timeZone,
            title = "Oneshot - monthly 14",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.MONTHLY,
            each = 14
        )

        assertEquals(
            remindie6.toNextShot(today6),
            NextShot(
                remindie = remindie6,
                target = LocalDateTime(2022, 1, 7, 18, 33),
                isFired = false
            )
        )
    }

    @Test
    fun getShots_test() {
        val timeZone = TimeZone.currentSystemDefault()
        val created = LocalDateTime(2020, 11, 22, 10, 0)
        val today = LocalDateTime(2020, 11, 23, 18, 0)

        val remindie1 = Remindie(
            createdTimestamp = 1,
            createdDate = created,
            targetTime = LocalDateTime(2020, 11, 23, 22, 0),
            creationTimeZone = timeZone,
            title = "Daily at 22:00",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.DAILY,
            each = 1
        )

        val remindie2 = Remindie(
            createdTimestamp = 2,
            createdDate = created,
            targetTime = LocalDateTime(2020, 10, 27, 1, 2),
            creationTimeZone = timeZone,
            title = "Weekly at 12:13",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.WEEKLY,
            each = 1
        )

        val remindie3 = Remindie(
            createdTimestamp = 3,
            createdDate = created,
            targetTime = LocalDateTime(2020, 12, 11, 23, 34),
            creationTimeZone = timeZone,
            title = "Monthly at 23:34",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.MONTHLY,
            each = 1
        )

        // test daily
        val shots1 = remindie1.getShots(
            from = LocalDateTime(2020, 11, 20, 22, 0).getDayStart(),
            to = LocalDateTime(2020, 11, 29, 22, 0).getDayEnd(),
            today = today
        )

        assertEquals(shots1.size, 7)

        // test weekly
        val shots2 = remindie2.getShots(
            from = LocalDateTime(2020, 11, 20, 22, 0).getDayStart(),
            to = LocalDateTime(2020, 12, 6, 22, 0).getDayEnd(),
            today = today
        )

        assertEquals(shots2.size, 2)

        // test monthly
        val shots3 = remindie3.getShots(
            from = LocalDateTime(2020, 11, 20, 22, 0).getDayStart(),
            to = LocalDateTime(2021, 3, 30, 12, 12).getDayEnd(),
            today = today
        )

        assertEquals(shots3.size, 4)
    }
}
