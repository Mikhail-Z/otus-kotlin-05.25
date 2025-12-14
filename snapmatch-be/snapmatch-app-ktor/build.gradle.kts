plugins {
    application
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx.serialization)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

ktor {
    docker {
        localImageName.set(project.name)
        imageTag.set(project.version.toString())
        jreVersion.set(JavaVersion.VERSION_21)
    }
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.yaml)
    implementation(libs.ktor.server.negotiation)
    implementation(libs.ktor.server.headers.response)
    implementation(libs.ktor.server.headers.caching)
    implementation(libs.ktor.server.headers.default)
    implementation(libs.ktor.server.calllogging)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // Serialization
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.datatype)

    // Logging
    implementation(libs.logback)
    
    // Local modules
    implementation(project(":snapmatch-common"))
    implementation(project(":snapmatch-biz"))
    implementation(project(":snapmatch-app-common"))
    implementation(project(":snapmatch-api-v1-jackson"))
    implementation(project(":snapmatch-api-v1-mappers"))
    implementation(project(":snapmatch-stubs"))
    implementation(project(":snapmatch-transport-ktor-ws"))
    implementation(project(":snapmatch-transport-ktor-rest"))

    // Testing
    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.server.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        // Add more packages as needed
    )
}