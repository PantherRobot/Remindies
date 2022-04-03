package com.sedsoftware.common.tools

import com.sedsoftware.common.domain.entity.NextShot
import platform.Foundation.NSDateComponents
import platform.Foundation.NSNumber
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSInteger

class AppleRemindiesAlarmManager : RemindiesAlarmManager {

    private val notificationCenter: UNUserNotificationCenter =
        UNUserNotificationCenter.currentNotificationCenter()

    override fun schedule(shot: NextShot) {

        // suspend required for completion callback?
        notificationCenter
            .requestAuthorizationWithOptions(UNAuthorizationOptionAlert) { granted, _ ->
                if (granted) {
                    val content: UNMutableNotificationContent = UNMutableNotificationContent().apply {
                        setTitle(shot.remindie.title)
                        setBody(shot.remindie.description)
                        setSound(UNNotificationSound.defaultSound)
                        setBadge(NSNumber(1))
                    }

                    val date: NSDateComponents = NSDateComponents()
                    date.day = shot.target.dayOfMonth.toNSInt()
                    date.month = shot.target.month.ordinal.toNSInt()
                    date.year = shot.target.year.toNSInt()
                    date.hour = shot.target.hour.toNSInt()
                    date.minute = shot.target.minute.toNSInt()

                    val trigger: UNCalendarNotificationTrigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
                        dateComponents = date,
                        repeats = false
                    )

                    val request = UNNotificationRequest.requestWithIdentifier(
                        identifier = shot.remindie.id.toString(),
                        content = content,
                        trigger = trigger
                    )

                    notificationCenter.addNotificationRequest(request) { error ->

                    }
                }
            }
    }

    override fun cancel(shot: NextShot) {
        notificationCenter
            .removePendingNotificationRequestsWithIdentifiers(listOf(shot.remindie.id.toString()))
    }

    private fun Int.toNSInt(): NSInteger =
        NSNumber(this).integerValue
}
