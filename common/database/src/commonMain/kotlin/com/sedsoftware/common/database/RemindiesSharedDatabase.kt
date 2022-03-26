package com.sedsoftware.common.database

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable

interface RemindiesSharedDatabase {

    fun observeAll(): Observable<List<RemindieEntity>>

    fun select(id: Long): Maybe<RemindieEntity>

    // @formatter:off
    fun insert(timestamp: Long, created: String, shot: String, timeZone: String, title: String, type: String, period: String, each: Int): Completable

    // @formatter:on
    fun delete(id: Long): Completable

    fun clear(): Completable
}
