package ai.snapmatch.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class KotlinLibraryConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            group = rootProject.group
            version = rootProject.version
            
            // Настройка JVM toolchain для совместимости
            extensions.findByType(KotlinJvmProjectExtension::class.java)?.apply {
                jvmToolchain {
                    languageVersion.set(JavaLanguageVersion.of(21))
                }
            }
            
            // Используем afterEvaluate для доступа к version catalog
            afterEvaluate {
                val libs = extensions.findByType(VersionCatalogsExtension::class.java)?.named("libs")
                dependencies {
                    libs?.let { catalog ->
                        catalog.findLibrary("kotlin-stdlib").ifPresent {
                            add("implementation", it)
                        }
                        catalog.findLibrary("mockk").ifPresent {
                            add("testImplementation", it)
                        }
                        catalog.findLibrary("kotlin-test-junit5").ifPresent {
                            add("testImplementation", it)
                        }
                        catalog.findLibrary("junit-jupiter-engine").ifPresent {
                            add("testRuntimeOnly", it)
                        }
                    }
                }
            }
            
            tasks.withType(Test::class.java).configureEach {
                useJUnitPlatform()
            }
            repositories {
                mavenCentral()
            }
        }
    }
}