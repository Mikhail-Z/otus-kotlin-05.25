pluginManagement {
    includeBuild("../build-plugin")
    plugins {
        id("build-jvm")
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
rootProject.name = "lessons"

include("m1l1-first")
include("m1l2-basic")
include("m1l3-func")
include("m1l4-oop")
include("m2l1-dsl")
include("m2l2-coroutines")
include("m2l3-flows")