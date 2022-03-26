plugins {
    `kotlin-dsl`
    id("com.louiscad.complete-kotlin") version "1.1.0"
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://kotlin.bintray.com/kotlinx/")
    }
}
