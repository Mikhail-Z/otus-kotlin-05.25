plugins {
    id("library-convention") apply false
}

group = "ai.snapmatch"
version = "0.0.1"

subprojects {
    plugins.apply("library-convention")
    group = rootProject.group
    version = rootProject.version
}

ext {
    val specDir = layout.projectDirectory.dir("../specs")
    set("spec-v1", specDir.file("specs-v1.yaml").toString())
}

