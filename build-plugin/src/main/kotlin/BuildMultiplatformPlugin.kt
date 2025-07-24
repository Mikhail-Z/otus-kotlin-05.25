package ru.otus.otuskotlin.marketplace.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KotlinMultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("org.jetbrains.kotlin.multiplatform")
        project.group = project.rootProject.group
        project.version = project.rootProject.version

        project.extensions.configure(KotlinMultiplatformExtension::class.java) {
            jvmToolchain(21)
            js(IR) {
                browser()
                nodejs()
            }
            linuxX64()
            macosX64()
            mingwX64()

            val commonMain = sourceSets.getByName("commonMain")
            commonMain.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }

            val commonTest = sourceSets.getByName("commonTest")
            commonTest.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test")
            }

            val jvmMain = sourceSets.getByName("jvmMain")
            jvmMain.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
            }

            val jvmTest = sourceSets.getByName("jvmTest")
            jvmTest.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }

            val jsMain = sourceSets.getByName("jsMain")
            jsMain.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
            }

            val jsTest = sourceSets.getByName("jsTest")
            jsTest.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-js")
            }
        }
    }
}
