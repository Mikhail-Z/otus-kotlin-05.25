package ai.snapmatch.app.ktor.v1.routes

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.ISnapmatchProcessor
import ai.snapmatch.transport.ktor.rest.v1.handlers.resumeGet
import ai.snapmatch.transport.ktor.rest.v1.handlers.resumeUpload
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyCreate
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyGet
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.State
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RouteConfigurationTest {

    @Test
    fun `should register resume routes correctly`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            firstArg<Context>().state = State.FINISHING
        }

        application {
            install(ContentNegotiation) {
                jackson()
            }
            routing {
                post("/resume/upload") { call.resumeUpload(appSettings) }
                post("/resume/get") { call.resumeGet(appSettings) }
            }
        }

        // Act
        val uploadResponse = client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        val getResponse = client.post("/resume/get") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        // Assert - routes are configured (not 404)
        assertNotEquals(HttpStatusCode.NotFound, uploadResponse.status)
        assertNotEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `should register vacancy routes correctly`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            firstArg<Context>().state = State.FINISHING
        }

        application {
            install(ContentNegotiation) {
                jackson()
            }
            routing {
                post("/vacancy/create") { call.vacancyCreate(appSettings) }
                post("/vacancy/get") { call.vacancyGet(appSettings) }
            }
        }

        // Act
        val createResponse = client.post("/vacancy/create") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        val getResponse = client.post("/vacancy/get") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        // Assert - routes are configured (not 404)
        assertNotEquals(HttpStatusCode.NotFound, createResponse.status)
        assertNotEquals(HttpStatusCode.NotFound, getResponse.status)
    }

    @Test
    fun `should handle invalid routes with 404`() = testApplication {
        // Arrange
        val appSettings = mockk<IAppSettings>(relaxed = true)

        application {
            install(ContentNegotiation) {
                jackson()
            }
            routing {
                post("/resume/upload") { call.resumeUpload(appSettings) }
            }
        }

        // Act
        val response = client.post("/nonexistent/route") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `should support POST method for all endpoints`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            firstArg<Context>().state = State.FINISHING
        }

        application {
            install(ContentNegotiation) {
                jackson()
            }
            routing {
                post("/resume/upload") { call.resumeUpload(appSettings) }
            }
        }

        // Act
        val postResponse = client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        // Assert - POST is supported
        assertNotEquals(HttpStatusCode.MethodNotAllowed, postResponse.status)
        assertNotEquals(HttpStatusCode.NotFound, postResponse.status)
    }

    @Test
    fun `should reject GET method for POST-only endpoints`() = testApplication {
        // Arrange
        val appSettings = mockk<IAppSettings>(relaxed = true)

        application {
            install(ContentNegotiation) {
                jackson()
            }
            routing {
                post("/resume/upload") { call.resumeUpload(appSettings) }
            }
        }

        // Act
        val getResponse = client.get("/resume/upload")

        // Assert - GET is not allowed
        assertEquals(HttpStatusCode.MethodNotAllowed, getResponse.status)
    }

    @Test
    fun `should support content negotiation for all routes`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            firstArg<Context>().state = State.FINISHING
        }

        application {
            install(ContentNegotiation) {
                jackson()
            }
            routing {
                post("/resume/upload") { call.resumeUpload(appSettings) }
            }
        }

        // Act
        val response = client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        // Assert - JSON content type is accepted
        assertNotEquals(HttpStatusCode.UnsupportedMediaType, response.status)
    }
}
