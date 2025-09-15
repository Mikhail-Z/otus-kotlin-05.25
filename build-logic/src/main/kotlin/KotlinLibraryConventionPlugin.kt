package com.mikhail.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val libs = project.extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

        project.pluginManager.apply("org.jetbrains.kotlin.jvm")

        project.dependencies {
            libs.findLibrary("kotlin-stdlib").ifPresent {
                add("implementation", it)
            }
            libs.findLibrary("mockk").ifPresent {
                add("testImplementation", it)
            }
            libs.findLibrary("junit-jupiter").ifPresent {
                add("testImplementation", it)
            }
        }

        project.tasks.withType(org.gradle.api.tasks.testing.Test::class.java).configureEach {
            useJUnitPlatform()
        }
    }
}
