package ru.otus.otuskotlin.marketplace.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class BuildJvmPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.apply("org.jetbrains.kotlin.jvm")

        project.group = project.rootProject.group
        project.version = project.rootProject.version

        project.extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }

        project.tasks.withType<Test>().configureEach {
            useJUnitPlatform()
        }

        project.repositories.apply {
            mavenCentral()
        }

        project.dependencies.apply {
            add("implementation", "org.jetbrains.kotlin:kotlin-stdlib")
        }
    }
}
