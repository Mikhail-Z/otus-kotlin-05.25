plugins {
    id("build-jvm")
}

val coroutinesVersion: String by project

//kotlin {
//    jvmToolchain(21)
//}


dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    testImplementation(kotlin("test-junit"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}