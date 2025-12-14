package ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins

import ai.snapmatch.api.v1.jackson.apiV1Mapper
import ai.snapmatch.api.v1.jackson.kotlinxDatetimeModule
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        jackson {
            // Register all required modules
            registerModule(KotlinModule.Builder().build())
            registerModule(JavaTimeModule())
            registerModule(kotlinxDatetimeModule)

            // Apply same config as apiV1Mapper
            enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }
}