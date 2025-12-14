plugins {
    application
    alias(libs.plugins.kotlinx.serialization)
}

application {
    mainClass.set("ai.snapmatch.app.kafka.MainKt")
}

dependencies {
    implementation(libs.kafka.client)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.atomicfu)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // Logging
    implementation(libs.logback)
    
    // Local modules
    implementation(project(":snapmatch-common"))
    implementation(project(":snapmatch-api-v1-jackson"))
    implementation(project(":snapmatch-api-v1-mappers"))
    implementation(project(":snapmatch-app-common"))
    implementation(project(":snapmatch-biz"))
    implementation(project(":snapmatch-stubs"))
    
    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent.jvm)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.ktor.server.test)
    testImplementation(libs.ktor.client.websockets)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "-Djdk.attach.allowAttachSelf=true"
    )
    systemProperty("mockk.instrumentation", "inline")
}