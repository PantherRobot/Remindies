plugins {
    id("multiplatform-setup")
    id("android-setup")
    id("com.squareup.sqldelight")
}

sqldelight {
    database("RemindieDatabase") {
        packageName = "com.sedsoftware.common.database"
    }
}

kotlin {

    sourceSets {
        commonMain {
            dependencies {
                implementation(Deps.Badoo.Reaktive.reaktive)
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.Squareup.SQLDelight.androidDriver)
                implementation(Deps.Squareup.SQLDelight.sqliteDriver)
            }
        }

        iosMain {
            dependencies {
                implementation(Deps.Squareup.SQLDelight.nativeDriver)
                implementation(Deps.Squareup.SQLDelight.sqliteDriver)
            }
        }
    }
}
