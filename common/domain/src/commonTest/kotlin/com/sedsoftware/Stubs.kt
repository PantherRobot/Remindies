package com.sedsoftware

import com.sedsoftware.common.domain.entity.Remindie
import com.sedsoftware.common.domain.type.RemindiePeriod
import com.sedsoftware.common.domain.type.RemindieType
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

object Stubs {

    // 5.11.2020 20:55 - Thursday
    private val today: LocalDateTime = LocalDateTime(2020, 11, 5, 10, 20)
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()

    val remindiesInOwnTimeZone: List<Remindie> = listOf(
        Remindie(
            createdTimestamp = 1,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 6, 15, 35),
            creationTimeZone = timeZone,
            title = "Oneshot - tomorrow",
            description = "Do something cool",
            type = RemindieType.AIRPORT,
            period = RemindiePeriod.NONE,
            each = 0
        ),

        Remindie(
            createdTimestamp = 2,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 5, 12, 0),
            creationTimeZone = timeZone,
            title = "Each 3 hours from today starting at 12:00",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.HOURLY,
            each = 3
        ),

        Remindie(
            createdTimestamp = 3,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 6, 8, 0),
            creationTimeZone = timeZone,
            title = "Daily - from tomorrow at 8:00",
            description = "Do something cool",
            type = RemindieType.CAFE,
            period = RemindiePeriod.DAILY,
            each = 1
        ),

        Remindie(
            createdTimestamp = 4,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 6, 11, 22),
            creationTimeZone = timeZone,
            title = "Daily - from tomorrow each 3 days at 11:22",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.DAILY,
            each = 3
        ),

        Remindie(
            createdTimestamp = 5,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 7, 21, 30),
            creationTimeZone = timeZone,
            title = "Each week from Saturday at 21:30",
            description = "Do something cool",
            type = RemindieType.GYM,
            period = RemindiePeriod.WEEKLY,
            each = 1
        ),

        Remindie(
            createdTimestamp = 6,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 8, 16, 0),
            creationTimeZone = timeZone,
            title = "Each two weeks from Sunday at 16:00",
            description = "Do something cool",
            type = RemindieType.DOCTOR,
            period = RemindiePeriod.WEEKLY,
            each = 2
        ),

        Remindie(
            createdTimestamp = 7,
            createdDate = today,
            targetTime = LocalDateTime(2020, 11, 8, 18, 0),
            creationTimeZone = timeZone,
            title = "Pay rent each month at 18:00",
            description = "Do something cool",
            type = RemindieType.PAY,
            period = RemindiePeriod.MONTHLY,
            each = 1
        ),

        Remindie(
            createdTimestamp = 8,
            createdDate = today,
            targetTime = LocalDateTime(2020, 12, 31, 23, 0),
            creationTimeZone = timeZone,
            title = "Congratulations each New Year night ^-^ at 23:00",
            description = "Do something cool",
            type = RemindieType.CALL,
            period = RemindiePeriod.YEARLY,
            each = 1
        ),
    )
}
