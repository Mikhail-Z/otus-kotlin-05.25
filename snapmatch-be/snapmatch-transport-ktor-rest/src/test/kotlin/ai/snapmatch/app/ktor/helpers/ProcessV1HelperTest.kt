package ai.snapmatch.app.ktor.helpers

import ai.snapmatch.api.v1.models.*
import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.ISnapmatchProcessor
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.State
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import ai.snapmatch.api.v1.mappers.output.toResumeGetResponse
import ai.snapmatch.transport.ktor.rest.helpers.processV1
import kotlin.test.Test
import kotlin.test.assertEquals

class ProcessV1HelperTest {

    fun TestApplicationBuilder.initializeJackson() {
        application {
            install(ContentNegotiation) {
                jackson {
                    registerModule(KotlinModule.Builder().build())
                    registerModule(JavaTimeModule())
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    enable(SerializationFeature.INDENT_OUTPUT)
                    writerWithDefaultPrettyPrinter()
                }
            }
        }
    }

    @Test
    fun `should respond with OK status on success`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
        }

        initializeJackson()

        application {
            routing {
                post("/test") {
                    call.processV1<ResumeGetRequestDto, ResumeGetResponseDto>(
                        appSettings = appSettings,
                        clazz = ResumeGetRequestDto::class,
                        logId = "test-endpoint"
                    ) {
                        toResumeGetResponse()
                    }
                }
            }
        }

        // Act
        val response = client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "getResume"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `should handle deserialization errors with 500`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        initializeJackson()

        application {
            routing {
                post("/test") {
                    call.processV1<ResumeGetRequestDto, ResumeGetResponseDto>(
                        appSettings = appSettings,
                        clazz = ResumeGetRequestDto::class,
                        logId = "test-endpoint"
                    ) {
                        toResumeGetResponse()
                    }
                }
            }
        }

        // Act - send invalid JSON
        val response = client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("invalid json")
        }

        // Assert
        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }

    @Test
    fun `should handle processor errors gracefully`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } throws RuntimeException("Processor error")

        initializeJackson()

        application {
            routing {
                post("/test") {
                    call.processV1<ResumeGetRequestDto, ResumeGetResponseDto>(
                        appSettings = appSettings,
                        clazz = ResumeGetRequestDto::class,
                        logId = "test-endpoint"
                    ) {
                        toResumeGetResponse()
                    }
                }
            }
        }

        // Act
        val response = client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "getResume"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals("Internal server error", response.bodyAsText())
    }

    @Test
    fun `should deserialize request correctly`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var capturedCommand: Command? = null
        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            capturedCommand = ctx.command
            ctx.state = State.FINISHING
        }

        initializeJackson()

        application {
            routing {
                post("/test") {
                    call.processV1<ResumeGetRequestDto, ResumeGetResponseDto>(
                        appSettings = appSettings,
                        clazz = ResumeGetRequestDto::class,
                        logId = "test-endpoint"
                    ) {
                        toResumeGetResponse()
                    }
                }
            }
        }

        // Act
        client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "getResume"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(Command.GET_RESUME, capturedCommand)
    }

    @Test
    fun `should respond with serialized response object`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.GET_RESUME
        }

        initializeJackson()

        application {
            routing {
                post("/test") {
                    call.processV1<ResumeGetRequestDto, ResumeGetResponseDto>(
                        appSettings = appSettings,
                        clazz = ResumeGetRequestDto::class,
                        logId = "test-endpoint"
                    ) {
                        toResumeGetResponse()
                    }
                }
            }
        }

        // Act
        val response = client.post("/test") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "getResume"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assert(body.contains("resumeGet") || body.contains("responseType"))
    }

    @Test
    fun `should handle missing content type`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        initializeJackson()

        application {
            routing {
                post("/test") {
                    call.processV1<ResumeGetRequestDto, ResumeGetResponseDto>(
                        appSettings = appSettings,
                        clazz = ResumeGetRequestDto::class,
                        logId = "test-endpoint"
                    ) {
                        toResumeGetResponse()
                    }
                }
            }
        }

        // Act - send without Content-Type
        val response = client.post("/test") {
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "getResume"
                }
            """.trimIndent())
        }

        // Assert - Ktor might still process it or return error
        assert(response.status == HttpStatusCode.OK || response.status == HttpStatusCode.InternalServerError)
    }
}
