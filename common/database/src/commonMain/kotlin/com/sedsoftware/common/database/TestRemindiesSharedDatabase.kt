package com.sedsoftware.common.database

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.observeOn
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.observeOn
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.firstOrDefault
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.observeOn
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.notNull
import com.badoo.reaktive.single.singleFromFunction
import com.badoo.reaktive.subject.behavior.BehaviorSubject

class TestRemindiesSharedDatabase(
    private val scheduler: Scheduler
) : RemindiesSharedDatabase {

    private val itemsSubject: BehaviorSubject<Map<Long, RemindieEntity>> = BehaviorSubject(emptyMap())
    private val itemsObservable: Observable<Map<Long, RemindieEntity>> = itemsSubject.observeOn(scheduler)
    private val testing: Testing = Testing()

    override fun observeAll(): Observable<List<RemindieEntity>> =
        itemsObservable.map { it.values.toList() }

    override fun getAll(): Single<List<RemindieEntity>> =
        itemsObservable.map { it.values.toList() }
            .firstOrDefault(emptyList())

    override fun select(id: Long): Maybe<RemindieEntity> =
        singleFromFunction { testing.select(id = id) }
            .notNull()
            .observeOn(scheduler)

    // @formatter:off
    override fun insert(createdTimestamp: Long, createdDate: String, targetTime: String, creationTimeZone: String, title: String, description: String, type: String, period: String, each: Int): Completable =
        execute { testing.add(createdTimestamp, createdDate, targetTime, creationTimeZone, title, description, type, period, each) }

    // @formatter:on
    override fun delete(id: Long): Completable =
        execute { testing.delete(id) }

    override fun clear(): Completable =
        execute { testing.clear() }

    private fun execute(block: () -> Unit): Completable =
        completableFromFunction(block)
            .observeOn(scheduler)

    inner class Testing {

        fun select(id: Long): RemindieEntity? =
            itemsSubject.value[id]

        fun selectRequired(id: Long): RemindieEntity =
            requireNotNull(select(id))

        // @formatter:off
        fun add(timestamp: Long, created: String, shot: String, timeZone: String, title: String, description: String, type: String, period: String, each: Int) {
            updateItems { items ->
                val nextId = items.keys.maxOrNull()?.plus(1L) ?: 1L
                val item = RemindieEntity(nextId, timestamp, created, shot, timeZone, title, description, type, period, each)
                items + (nextId to item)
            }
        }

        // @formatter:on
        fun delete(id: Long) {
            updateItems { it - id }
        }

        fun clear() {
            updateItems { emptyMap() }
        }

        fun getLastInsertId(): Long? =
            itemsSubject.value.values.lastOrNull()?.id

        private fun updateItems(func: (Map<Long, RemindieEntity>) -> Map<Long, RemindieEntity>) {
            itemsSubject.onNext(func(itemsSubject.value))
        }
    }
}
