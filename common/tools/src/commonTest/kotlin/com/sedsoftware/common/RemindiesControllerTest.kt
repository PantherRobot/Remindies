package com.sedsoftware.common

import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.observable.firstOrError
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.trampolineScheduler
import com.badoo.reaktive.single.blockingGet
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MockSettings
import com.sedsoftware.common.database.RemindiesSharedDatabase
import com.sedsoftware.common.database.TestRemindiesSharedDatabase
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.type.RemindiePeriod
import com.sedsoftware.common.tools.RemindiesSharedSettings
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plusPeriod
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSettingsApi
class RemindiesControllerTest {

    private lateinit var database: RemindiesSharedDatabase
    private lateinit var settings: RemindiesSharedSettings
    private lateinit var manager: RemindiesAlarmManagerTest
    private lateinit var controller: RemindiesController

    private val testScheduler: Scheduler =
        TestScheduler()

    private val testUserDefaultTimeZone: TimeZone =
        TimeZone.of("GMT+3")

    // 03.04.2020 - Sunday - week start if start from Sunday
    // 04.04.2020 - Monday - week start if not start from Sunday - today
    // 05.04.2020 - Tuesday - tomorrow
    // ...
    // 09.04.2020 - Saturday
    // 10.04.2020 - Sunday

    private val today: LocalDateTime =
        LocalDateTime(2022, 4, 4, 12, 34)

    private val testTarget: LocalDateTime =
        LocalDateTime(2022, 4, 5, 10, 56)


    @BeforeTest
    fun setUp() {
        database = TestRemindiesSharedDatabase(trampolineScheduler)
        settings = RemindiesSharedSettings(MockSettings())
        manager = RemindiesAlarmManagerTest()
        controller = RemindiesController(database, manager, settings, trampolineScheduler, testUserDefaultTimeZone, today)
    }

    @Test
    fun test_getForToday() {
        val target1: LocalDateTime = LocalDateTime(2022, 4, 4, 8, 9)
        val target2: LocalDateTime = LocalDateTime(2022, 4, 4, 19, 20)

        var sub = controller.add("Test reminder1", "TEST1", target1, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        sub.assertNotError()

        sub = controller.add("Test reminder2", "TEST2", target2, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        sub.assertNotError()

        assertTrue { manager.nearestShot != null }

        val values: List<NextShot> = controller.getShotsForToday()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 2 }
            assertTrue { first().isFired }
            assertFalse { last().isFired }
        }
    }

    @Test
    fun test_getForDate() {
        val target1: LocalDateTime = LocalDateTime(2022, 4, 4, 8, 9)
        val target2: LocalDateTime = LocalDateTime(2022, 4, 4, 19, 20)
        val tomorrow: LocalDateTime = LocalDateTime(2022, 4, 5, 12, 34)

        var sub = controller.add("Test reminder1", "TEST1", target1, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        sub.assertNotError()

        sub = controller.add("Test reminder2", "TEST2", target2, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        sub.assertNotError()

        assertTrue { manager.nearestShot != null }

        val values: List<NextShot> = controller.getShotsForDate(tomorrow)
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 2 }
            assertFalse { first().isFired }
            assertFalse { last().isFired }
        }
    }

    @Test
    fun test_tomorrow_non_repeatable() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.NONE, 0)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        manager.nearestShot?.let { shot ->
            assertEquals(shot.target.year, 2022)
            assertEquals(shot.target.monthNumber, 4)
            assertEquals(shot.target.dayOfMonth, 5)
            assertEquals(shot.target.hour, 10)
            assertEquals(shot.target.minute, 56)
        }

        sub.assertNotError()
    }

    // --- BASIC DAILY SETUPS

    @Test
    fun singleRemindieDaily_currentWeek_startFromMonday_test() {
        settings.startFromSunday = false

        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForCurrentWeek()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 6 }
            assertEquals(first().target, LocalDateTime(2022, 4, 5, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 10, 10, 56))
        }
    }

    @Test
    fun singleRemindieDaily_currentWeek_startFromSunday_test() {
        settings.startFromSunday = true

        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForCurrentWeek()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 5 }
            assertEquals(first().target, LocalDateTime(2022, 4, 5, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 9, 10, 56))
        }
    }

    @Test
    fun singleRemindieDaily_nextWeek_startFromMonday_test() {
        settings.startFromSunday = false

        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForWeek(LocalDateTime(2022, 4, 13, 1, 1))
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 7 }
            assertEquals(first().target, LocalDateTime(2022, 4, 11, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 17, 10, 56))
        }
    }

    @Test
    fun singleRemindieDaily_nextWeek_startFromSunday_test() {
        settings.startFromSunday = true

        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForWeek(LocalDateTime(2022, 4, 13, 1, 1))
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 7 }
            assertEquals(first().target, LocalDateTime(2022, 4, 10, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 16, 10, 56))
        }
    }


    @Test
    fun singleRemindieDaily_currentMonth_test() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForCurrentMonth()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 26 }
            assertEquals(first().target, LocalDateTime(2022, 4, 5, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 30, 10, 56))
        }
    }

    @Test
    fun singleRemindieDaily_nextMonth_test() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForMonth(LocalDateTime(2022, 5, 4, 11, 11))
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 31 }
            assertEquals(first().target, LocalDateTime(2022, 5, 1, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 5, 31, 10, 56))
        }
    }

    @Test
    fun singleRemindieDaily_currentYear_test() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForCurrentYear()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertEquals(first().target, LocalDateTime(2022, 4, 5, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 12, 31, 10, 56))
        }
    }

    @Test
    fun singleRemindieDaily_nextYear_test() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForYear(LocalDateTime(2023, 6, 13, 22, 22))
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertEquals(first().target, LocalDateTime(2023, 1, 1, 10, 56))
            assertEquals(last().target, LocalDateTime(2023, 12, 31, 10, 56))
        }
    }

    // --- WEEKLY - EACH 2 DAYS

    @Test
    fun singleRemindieTwoDays_currentWeek_startFromMonday_test() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 2)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForCurrentWeek()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 3 }
            assertFalse { first().isFired }
            assertFalse { last().isFired }
            assertEquals(first().target, LocalDateTime(2022, 4, 5, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 9, 10, 56))
        }
    }

    @Test
    fun singleRemindieTwoDays_currentWeek_startFromSunday_test() {
        val sub = controller.add("Test reminder", "TEST", testTarget, RemindiePeriod.DAILY, 2)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        sub.assertNotError()

        val values: List<NextShot> = controller.getShotsForCurrentWeek()
            .subscribeOn(testScheduler)
            .firstOrError()
            .blockingGet()

        with(values) {
            assertTrue { isNotEmpty() }
            assertTrue { size == 3 }
            assertEquals(first().target, LocalDateTime(2022, 4, 5, 10, 56))
            assertEquals(last().target, LocalDateTime(2022, 4, 9, 10, 56))
        }
    }

    // --- SCHEDULE AND CANCEL

    @Test
    fun scheduleAndCancel_test() {
        val laterTarget: LocalDateTime =
            LocalDateTime(2022, 6, 17, 11, 22)

        val earlierTarget: LocalDateTime =
            LocalDateTime(2022, 5, 10, 2, 3)

        var sub = controller.add("Test reminder later", "TEST", laterTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        assertTrue { manager.nearestShot != null }

        manager.nearestShot?.let { shot ->
            assertEquals(shot.target.year, 2022)
            assertEquals(shot.target.monthNumber, 6)
            assertEquals(shot.target.dayOfMonth, 17)
            assertEquals(shot.target.hour, 11)
            assertEquals(shot.target.minute, 22)
        }

        sub.assertNotError()

        sub = controller.add("Test reminder earlier", "TEST", earlierTarget, RemindiePeriod.DAILY, 1)
            .subscribeOn(testScheduler)
            .test()

        manager.nearestShot?.let { shot ->
            assertEquals(shot.target.year, 2022)
            assertEquals(shot.target.monthNumber, 5)
            assertEquals(shot.target.dayOfMonth, 10)
            assertEquals(shot.target.hour, 2)
            assertEquals(shot.target.minute, 3)
        }

        sub.assertNotError()

        sub = controller.remove(manager.nearestShot!!.remindie)
            .subscribeOn(testScheduler)
            .test()

        manager.nearestShot?.let { shot ->
            assertEquals(shot.target.year, 2022)
            assertEquals(shot.target.monthNumber, 6)
            assertEquals(shot.target.dayOfMonth, 17)
            assertEquals(shot.target.hour, 11)
            assertEquals(shot.target.minute, 22)
        }
    }
}
