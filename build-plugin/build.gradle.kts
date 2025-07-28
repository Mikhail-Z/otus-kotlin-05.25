plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

gradlePlugin {
    plugins {
        register("build-jvm") {
            id = "build-jvm"
            implementationClass = "ru.otus.otuskotlin.marketplace.plugin.BuildJvmPlugin"
        }
        register("build-multiplatform") {
            id = "build-multiplatform"
            implementationClass = "ru.otus.otuskotlin.marketplace.plugin.BuildMultiplatformPlugin"
        }
    }
}

dependencies {
    implementation(libs.plugin.kotlin)
}

repositories {
    mavenCentral()
}