plugins {
    id("library-convention") apply false
    id("base") // Добавляет задачи clean, check, build и т.д.
}

group = "ai.snapmatch"
version = "0.0.1"

subprojects {
    plugins.apply("library-convention")
    group = rootProject.group
    version = rootProject.version
}

// Задача clean для родительского проекта зависит от clean всех подпроектов
tasks.clean {
    dependsOn(subprojects.map { it.tasks.named("clean") })
}

// Задача test для родительского проекта зависит от test всех подпроектов
tasks.register("test") {
    dependsOn(subprojects.map { it.tasks.named("test") })
    group = "verification"
    description = "Runs tests for all subprojects"
}

ext {
    val specDir = layout.projectDirectory.dir("../specs")
    set("spec-v1", specDir.file("specs-v1.yaml").toString())
}

