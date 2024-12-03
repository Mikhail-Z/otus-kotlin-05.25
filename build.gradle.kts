plugins {
    kotlin("jvm")
}

group = "me.zabelin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}