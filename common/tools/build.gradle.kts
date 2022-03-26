plugins {
    id("multiplatform-setup")
    id("android-setup")
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":common:database"))
                implementation(project(":common:domain"))

                implementation(Deps.KMM.MultiplatformSettings.settings)
                implementation(Deps.KMM.MultiplatformSettings.test)
            }
        }

        androidMain {
            dependencies {
                implementation(Deps.AndroidX.Preferences.preferences)
            }
        }
    }
}
