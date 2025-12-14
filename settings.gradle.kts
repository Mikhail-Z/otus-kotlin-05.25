pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
    }
}

rootProject.name = "snapmatch"


plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

// Подключение сервиса
includeBuild("snapmatch-be")

// Подключение build-logic как composite build
includeBuild("build-logic")
