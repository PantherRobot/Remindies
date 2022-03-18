plugins {
    id("com.android.library")
    id("kotlin-multiplatform")
}

kotlin {
    android()
    ios()

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(Deps.Badoo.Reaktive.reaktive)
                implementation(Deps.JetBrains.DateTime.dateTime)
            }

            kotlin.srcDirs("src/commonMain/kotlinX")
        }

        named("commonTest") {
            dependencies {
                implementation(Deps.JetBrains.DateTime.dateTime)
                implementation(Deps.JetBrains.Kotlin.testCommon)
                implementation(Deps.JetBrains.Kotlin.testAnnotationsCommon)
            }
        }

        named("androidTest") {
            dependencies {
                implementation(Deps.JetBrains.Kotlin.testJunit)
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}
