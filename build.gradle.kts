plugins {
    id("com.soywiz.korge")
    kotlin("kapt") version "1.4.31"
}

korge {
    targetJvm()
    targetJs()
}

repositories {
    maven("https://jitpack.io")
}

val ldtkApiVersion: String by project

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.lehaine.kt-ldtk-api:ldtk-api:$ldtkApiVersion")
            }
        }

        val jvmMain by getting {
            dependencies {
                configurations.all { // kapt has an issue with determining the correct KMM library, so we need to help it
                    if (name.contains("kapt")) {
                        attributes.attribute(
                            org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.attribute,
                            org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType.jvm // pass in the JVM
                        )
                    }
                }
                configurations["kapt"].dependencies.add(project.dependencies.create("com.lehaine.kt-ldtk-api:ldtk-processor:$ldtkApiVersion"))
            }
        }
    }
}