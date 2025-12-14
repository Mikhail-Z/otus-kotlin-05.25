plugins {
    alias(libs.plugins.openapi.generator)
}

sourceSets {
    main {
        java.srcDir(layout.buildDirectory.dir("generate-resources/main/src/main/kotlin"))
    }
}

openApiGenerate {
    val openapiGroup = "${rootProject.group}.api.v1"
    generatorName.set("kotlin")
    packageName.set(openapiGroup)
    apiPackage.set("$openapiGroup.api")
    modelPackage.set("$openapiGroup.models")
    invokerPackage.set("$openapiGroup.invoker")
    inputSpec.set(parent!!.extra["spec-v1"] as String)

    globalProperties.apply {
        put("models", "")
        put("modelDocs", "false")
    }

    configOptions.set(
        mapOf(
            "dateLibrary" to "string",
            "enumPropertyNaming" to "UPPERCASE",
            "serializationLibrary" to "jackson",
            "collectionType" to "list"
        )
    )

    // Переопределяем маппинг типов для полей с format: date-time
    typeMappings.set(
        mapOf(
            "DateTime" to "kotlinx.datetime.Instant",
            "OffsetDateTime" to "kotlinx.datetime.Instant"
        )
    )

    // Переопределяем маппинг импортов
    importMappings.set(
        mapOf(
            "kotlinx.datetime.Instant" to "kotlinx.datetime.Instant"
        )
    )

    // Дополнительные свойства для корректной генерации
    additionalProperties.set(
        mapOf(
            "dateLibrary" to "string"
        )
    )
}

dependencies {
    // Jackson для сериализации (нужно для сгенерированных моделей)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.datatype)
    implementation(libs.kotlinx.datetime)
    implementation(libs.jackson.kotlin)
}

tasks.compileKotlin {
    dependsOn("openApiGenerate")
}