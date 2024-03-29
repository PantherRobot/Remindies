package com.sedsoftware.common.database

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.single.Single

interface RemindiesSharedDatabase {

    fun observeAll(): Observable<List<RemindieEntity>>

    fun getAll(): Single<List<RemindieEntity>>

    fun select(id: Long): Maybe<RemindieEntity>

    // @formatter:off
    fun insert(createdTimestamp: Long, createdDate: String, targetTime: String, creationTimeZone: String, title: String, description: String, type: String, period: String, each: Int): Completable

    // @formatter:on
    fun delete(id: Long): Completable

    fun clear(): Completable
}
