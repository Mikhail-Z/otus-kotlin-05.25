plugins {
    kotlin("jvm")
}

dependencies {

    implementation(libs.coroutines.core)

    implementation(libs.kotlin.stdlib)
    
    implementation(project(":snapmatch-common"))
    implementation(project(":snapmatch-app-common"))
    implementation(project(":snapmatch-stubs"))
    //implementation(project(":snapmatch-transport-ktor-ws"))

    testImplementation(kotlin("test"))
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test.junit5)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    )
}