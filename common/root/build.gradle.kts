import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("multiplatform-setup")
    id("android-setup")
}

kotlin {
    ios {
        binaries {
            framework {
                baseName = "Remindies"
                linkerOpts.add("-lsqlite3")
                export(project(":common:database"))
                export(project(":common:domain"))
                export(Deps.ArkIvanov.Decompose.decompose)
                export(Deps.ArkIvanov.MVIKotlin.mvikotlinMain)
                export(Deps.ArkIvanov.Essenty.lifecycle)
            }
        }
    }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":common:database"))
                implementation(project(":common:domain"))
                implementation(Deps.ArkIvanov.Decompose.decompose)
                implementation(Deps.ArkIvanov.MVIKotlin.mvikotlin)
                implementation(Deps.Badoo.Reaktive.reaktive)
            }
        }

        named("iosMain") {
            dependencies {
                api(project(":common:database"))
                api(project(":common:domain"))
                api(Deps.ArkIvanov.Decompose.decompose)
                api(Deps.ArkIvanov.MVIKotlin.mvikotlinMain)
                api(Deps.ArkIvanov.Essenty.lifecycle)
            }
        }
    }
}
