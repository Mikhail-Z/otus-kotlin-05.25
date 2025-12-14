plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("library-convention") {
            id = "library-convention"
            implementationClass = "ai.snapmatch.plugin.KotlinLibraryConventionPlugin"
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.plugin.kotlin)
    implementation(libs.plugin.binaryCompatibilityValidator)
}