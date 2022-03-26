package com.sedsoftware.common.database

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

@Suppress("FunctionName")
fun RemindiesDatabaseDriver(): SqlDriver =
    NativeSqliteDriver(RemindieDatabase.Schema, "RemindieDatabase.db")
