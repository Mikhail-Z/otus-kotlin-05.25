plugins {
    kotlin("jvm")
    id("library-convention")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.coroutines.core)
    
    // Snapmatch modules
    implementation(project(":snapmatch-common"))
    
    // Tests
    testImplementation(libs.coroutines.test)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED"
    )
}