package com.sedsoftware.common

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.flatMapCompletable
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.subscribeOn
import com.badoo.reaktive.scheduler.ioScheduler
import com.russhwolf.settings.ExperimentalSettingsApi
import com.sedsoftware.common.database.RemindieEntity
import com.sedsoftware.common.database.RemindiesSharedDatabase
import com.sedsoftware.common.domain.entity.NextShot
import com.sedsoftware.common.domain.entity.Remindie
import com.sedsoftware.common.domain.entity.getShots
import com.sedsoftware.common.domain.entity.toNextShot
import com.sedsoftware.common.domain.entity.updateTimeZone
import com.sedsoftware.common.domain.type.RemindiePeriod
import com.sedsoftware.common.domain.type.RemindieType
import com.sedsoftware.common.tools.RemindiesAlarmManager
import com.sedsoftware.common.tools.RemindiesSharedSettings
import com.sedsoftware.common.tools.RemindiesTypeChecker
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.getMonthEnd
import kotlinx.datetime.getMonthStart
import kotlinx.datetime.getWeekEnd
import kotlinx.datetime.getWeekStart
import kotlinx.datetime.getYearEnd
import kotlinx.datetime.getYearStart
import kotlinx.datetime.sameDayAs
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalSettingsApi
class RemindiesController constructor(
    private val database: RemindiesSharedDatabase,
    private val manager: RemindiesAlarmManager,
    private val settings: RemindiesSharedSettings
) {

    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
    private val today: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone)
    private val typeChecker: RemindiesTypeChecker = RemindiesTypeChecker()

    fun add(title: String, description: String, target: LocalDateTime, period: RemindiePeriod, each: Int): Completable =
        completableFromFunction {
            database.insert(
                createdTimestamp = today.toInstant(timeZone).toEpochMilliseconds(),
                createdDate = today.toString(),
                targetTime = target.toString(),
                creationTimeZone = timeZone.id,
                title = title,
                description = description,
                type = RemindieType.toString(typeChecker.getType(title)),
                period = period.str,
                each = each
            )
        }
            .subscribeOn(ioScheduler)

    fun remove(remindie: Remindie): Completable =
        completableFromFunction {
            val next: NextShot = remindie.toNextShot(today)
            manager.cancel(next)
            database.delete(remindie.id)
        }
            .subscribeOn(ioScheduler)

    fun rescheduleNext(): Completable =
        database.observeAll()
            .flatMapCompletable(::getAllAndReschedule)
            .subscribeOn(ioScheduler)

    fun getShotsForToday(): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toNextShot(it) }
                    .filter { !it.isFired }
                    .filter { it.target.sameDayAs(today) }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForDate(date: LocalDateTime): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toNextShot(it) }
                    .filter { !it.isFired }
                    .filter { it.target.sameDayAs(date) }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForCurrentWeek(): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toRemindie(it) }
                    .fold(mutableListOf<NextShot>()) { acc, remindie ->
                        acc.apply {
                            addAll(
                                remindie.getShots(
                                    from = today.getWeekStart(settings.startFromSunday),
                                    to = today.getWeekEnd(settings.startFromSunday),
                                    today = today
                                )
                            )
                        }
                    }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForWeek(date: LocalDateTime): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toRemindie(it) }
                    .fold(mutableListOf<NextShot>()) { acc, remindie ->
                        acc.apply {
                            addAll(
                                remindie.getShots(
                                    from = date.getWeekStart(settings.startFromSunday),
                                    to = date.getWeekEnd(settings.startFromSunday),
                                    today = today
                                )
                            )
                        }
                    }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForCurrentMonth(): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toRemindie(it) }
                    .fold(mutableListOf<NextShot>()) { acc, remindie ->
                        acc.apply {
                            addAll(
                                remindie.getShots(
                                    from = today.getMonthStart(),
                                    to = today.getMonthEnd(),
                                    today = today
                                )
                            )
                        }
                    }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForMonth(date: LocalDateTime): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toRemindie(it) }
                    .fold(mutableListOf<NextShot>()) { acc, remindie ->
                        acc.apply {
                            addAll(
                                remindie.getShots(
                                    from = date.getMonthStart(),
                                    to = date.getMonthEnd(),
                                    today = today
                                )
                            )
                        }
                    }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForCurrentYear(): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toRemindie(it) }
                    .fold(mutableListOf<NextShot>()) { acc, remindie ->
                        acc.apply {
                            addAll(
                                remindie.getShots(
                                    from = today.getYearStart(),
                                    to = today.getYearEnd(),
                                    today = today
                                )
                            )
                        }
                    }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    fun getShotsForYear(date: LocalDateTime): Observable<List<NextShot>> =
        database.observeAll()
            .map { entities: List<RemindieEntity> ->
                entities
                    .map { toRemindie(it) }
                    .fold(mutableListOf<NextShot>()) { acc, remindie ->
                        acc.apply {
                            addAll(
                                remindie.getShots(
                                    from = date.getYearStart(),
                                    to = date.getYearEnd(),
                                    today = today
                                )
                            )
                        }
                    }
                    .sortedBy { it.target }
            }
            .subscribeOn(ioScheduler)

    private fun getAllAndReschedule(list: List<RemindieEntity>): Completable =
        completableFromFunction {
            val shots = list
                .map { toNextShot(it) }
                .filter { !it.isFired }
                .sortedBy { it.target }

            if (shots.isNotEmpty()) {
                val next = if (settings.timeZoneDependent) {
                    shots.first().updateTimeZone(timeZone)
                } else {
                    shots.first()
                }

                manager.schedule(next)
            }
        }

    private fun toRemindie(entity: RemindieEntity): Remindie =
        Remindie(
            id = entity.id,
            createdTimestamp = entity.createdTimestamp,
            createdDate = LocalDateTime.parse(entity.createdDate),
            targetTime = LocalDateTime.parse(entity.targetTime),
            creationTimeZone = TimeZone.of(entity.creationTimeZone),
            title = entity.title,
            description = entity.description,
            type = RemindieType.fromString(entity.type),
            period = RemindiePeriod.fromString(entity.period),
            each = entity.each
        )

    private fun toNextShot(entity: RemindieEntity): NextShot =
        toRemindie(entity)
            .toNextShot(today)
}
