plugins {
    id("library-convention")
}

group = "ai.snapmatch"
version = "0.0.1"

subprojects {
    apply(plugin = "library-convention")
    group = rootProject.group
    version = rootProject.version
}

//tasks {
//    register("check" ) {
//        group = "verification"
//        dependsOn(gradle.includedBuild("ok-marketplace-be").task(":check"))
//    }
//}
