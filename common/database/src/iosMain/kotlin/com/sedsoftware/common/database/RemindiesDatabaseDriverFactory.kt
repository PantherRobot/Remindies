package com.sedsoftware.common.database

import android.content.Context
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

@Suppress("FunctionName")
fun RemindiesDatabaseDriver(context: Context): SqlDriver =
    NativeSqliteDriver(RemindieDatabase.Schema, "RemindieDatabase.db")
