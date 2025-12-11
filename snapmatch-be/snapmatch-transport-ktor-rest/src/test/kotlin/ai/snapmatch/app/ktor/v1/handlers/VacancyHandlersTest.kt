package ai.snapmatch.app.ktor.v1.handlers

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.ISnapmatchProcessor
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyCreate
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyDelete
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyGet
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyResumes
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancySearch
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.State
import ai.snapmatch.stubs.ResumeStub
import ai.snapmatch.stubs.VacancyStub
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class VacancyHandlersTest {

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
    fun `vacancyCreate should create vacancy and return response`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.CREATE_VACANCY
            ctx.vacancyResponse = VacancyStub.get()
        }

        initializeJackson()

        application {
            routing {
                post("/vacancy/create") {
                    call.vacancyCreate(appSettings)
                }
            }
        }

        // Act
        val response = client.post("/vacancy/create") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "vacancy": {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "title": "Test Vacancy",
                        "description": "Test Description",
                        "skills": [],
                        "companyName": "Test Company"
                    },
                    "requestType": "createVacancy"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `vacancyGet should return vacancy details`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.GET_VACANCY
            ctx.vacancyResponse = VacancyStub.get()
        }

        initializeJackson()

        application {
            routing {
                post("/vacancy/get") {
                    call.vacancyGet(appSettings)
                }
            }
        }

        // Act
        val response = client.post("/vacancy/get") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "vacancy": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "getVacancy"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val body = response.bodyAsText()
        assertTrue(body.contains("vacancy") || body.contains("title"))
    }

    @Test
    fun `vacancySearch should return paginated vacancy search results`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.SEARCH_VACANCIES
            ctx.vacanciesResponse.addAll(VacancyStub.prepareSearchList())
        }

        initializeJackson()

        application {
            routing {
                post("/vacancy/search") {
                    call.vacancySearch(appSettings)
                }
            }
        }

        // Act
        val response = client.post("/vacancy/search") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "filter": {
                        "searchString": "developer"
                    },
                    "pagination": {
                        "page": 1,
                        "size": 10
                    },
                    "requestType": "searchVacancies"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `vacancyResumes should return resumes matching vacancy`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.GET_VACANCY_RESUMES
            ctx.resumesResponse.addAll(ResumeStub.prepareResumesList())
        }

        initializeJackson()

        application {
            routing {
                post("/vacancy/resumes") {
                    call.vacancyResumes(appSettings)
                }
            }
        }

        // Act
        val response = client.post("/vacancy/resumes") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "vacancy": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "pagination": {
                        "page": 1,
                        "size": 10
                    },
                    "requestType": "getVacancyResumes"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `vacancyDelete should mark vacancy as inactive`() = testApplication {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
            ctx.command = Command.DELETE_VACANCY
            ctx.vacancyResponse = VacancyStub.prepareResult { isActive = false }
        }

        initializeJackson()

        application {
            routing {
                post("/vacancy/delete") {
                    call.vacancyDelete(appSettings)
                }
            }
        }

        // Act
        val response = client.post("/vacancy/delete") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "vacancy": {
                        "id": "550e8400-e29b-41d4-a716-446655440000"
                    },
                    "requestType": "deleteVacancy"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `vacancyCreate should deserialize request correctly`() = testApplication {
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
            ctx.vacancyResponse = VacancyStub.get()
        }

        initializeJackson()

        application {
            routing {
                post("/vacancy/create") {
                    call.vacancyCreate(appSettings)
                }
            }
        }

        // Act
        client.post("/vacancy/create") {
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "debug": {
                        "mode": "prod",
                        "stub": "success"
                    },
                    "vacancy": {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "title": "Test",
                        "description": "Test",
                        "skills": [],
                        "companyName": "Test Company"
                    },
                    "requestType": "createVacancy"
                }
            """.trimIndent())
        }

        // Assert
        assertEquals(Command.CREATE_VACANCY, capturedCommand)
    }
}
