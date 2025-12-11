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

include("snapmatch-api-v1-jackson")
include("snapmatch-api-v1-mappers")
include("snapmatch-common")
include("snapmatch-stubs")
include("snapmatch-biz")
include("snapmatch-app-common")
include("snapmatch-app-ktor")
include("snapmatch-transport-ktor-rest")
include("snapmatch-transport-ktor-ws")