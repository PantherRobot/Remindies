plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

dependencies {
    implementation(Deps.JetBrains.Kotlin.gradlePlugin)
    implementation(Deps.Android.Tools.Build.gradlePlugin)
    implementation(Deps.Squareup.SQLDelight.gradlePlugin)
}

kotlin {
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}