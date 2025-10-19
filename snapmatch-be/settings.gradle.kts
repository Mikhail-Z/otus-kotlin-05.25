rootProject.name = "snapmatch-be"

includeBuild("../build-logic")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

include("api-v1-jackson")
include("api-v1-mappers")
include("common")
include("stubs")