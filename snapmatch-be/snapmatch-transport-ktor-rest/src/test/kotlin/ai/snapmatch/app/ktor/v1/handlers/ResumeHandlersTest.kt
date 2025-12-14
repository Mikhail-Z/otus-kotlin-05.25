package ai.snapmatch.app.ktor.v1.handlers

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.ISnapmatchProcessor
import ai.snapmatch.app.ktor.routes.v1.rest.v1Resume
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.State
import ai.snapmatch.stubs.ResumeStub
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
import kotlin.test.DefaultAsserter.assertNotNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResumeHandlersTest {
    private val mockProcessor = mockk<ISnapmatchProcessor>()
    private val appSettings = mockk<IAppSettings> {
        every { processor } returns mockProcessor
    }

    fun TestApplicationBuilder.initializeApplication() {
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
            routing {
                v1Resume(appSettings)
            }
        }
    }

    @Test
    fun `resumeUpload should handle upload request and return response`() = testApplication {
        initializeApplication()

        // Arrange
        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.UPLOAD_RESUME
            ctx.resumeResponse = ResumeStub.get()
        }

        // Act
        val response = client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "vacancyId": "550e8400-e29b-41d4-a716-446655440001",
                        "file": {
                            "contentB64": "dGVzdA==",
                            "fileName": "test.pdf"
                        }
                    },
                    "requestType": "uploadResume"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `resumeGet should handle get request`() = testApplication {
        initializeApplication()

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.GET_RESUME
            ctx.resumeResponse = ResumeStub.get()
        }

        // Act
        val response = client.post("/resume/get") {
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
        assertTrue(body.contains("resume") || body.contains("fileName"))
    }

    @Test
    fun `resumeMy should return paginated list of user resumes`() = testApplication {
        initializeApplication()

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.GET_MY_RESUMES
            ctx.resumesResponse.addAll(ResumeStub.prepareMyResumesList())
        }

        // Act
        val response = client.post("/resume/my") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "pagination": {
                        "page": 1,
                        "size": 10
                    },
                    "requestType": "getMyResumes"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `resumeDownload should return PDF file bytes`() = testApplication {
        initializeApplication()
        // Act
        val response = client.post("/resume/download")

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Application.Pdf, response.contentType())
    }

    @Test
    fun `resumeDownload should set Content-Disposition header`() = testApplication {
        initializeApplication()
        // Arrange
        val appSettings = mockk<IAppSettings>(relaxed = true)

        // Act
        val response = client.post("/resume/download")

        // Assert
        val contentDisposition = response.headers[HttpHeaders.ContentDisposition]
        assertNotNull("", contentDisposition)
        assertTrue(contentDisposition!!.contains("attachment"))
        assertTrue(contentDisposition.contains("filename"))
    }

    @Test
    fun `resumeUpload should deserialize request correctly`() = testApplication {
        initializeApplication()
        // Arrange

        var capturedCommand: Command? = null
        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            capturedCommand = ctx.command
            ctx.state = State.FINISHING
            ctx.resumeResponse = ResumeStub.get()
        }

        // Act
        client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "resume": {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "vacancyId": "550e8400-e29b-41d4-a716-446655440001",
                        "file": {
                            "contentB64": "dGVzdA==",
                            "fileName": "test.pdf"
                        }
                    },
                    "requestType": "uploadResume"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(Command.UPLOAD_RESUME, capturedCommand)
    }

    @Test
    fun `resumeGet should return response with correct responseType`() = testApplication {
        initializeApplication()
        // Arrange

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.GET_RESUME
            ctx.resumeResponse = ResumeStub.get()
        }

        // Act
        val response = client.post("/resume/get") {
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
        val body = response.bodyAsText()
        assertTrue(body.contains("resumeGet") || body.contains("responseType"))
    }
}
