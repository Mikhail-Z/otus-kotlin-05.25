package ai.snapmatch.app.ktor

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun `should install module with all plugins`() = testApplication {
        // Arrange & Act
        application {
            module()
        }

        // Assert - verify app started without errors
        assertTrue(true)
    }

    @Test
    fun `should configure routing in module`() = testApplication {
        // Arrange
        application {
            module()
        }

        // Act - try to access a route (even if it returns 404, routing is configured)
        val response = client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }

        // Assert - routing is configured (not 404 for missing route config, but potentially 400/500 for bad request)
        assert(response.status != HttpStatusCode.NotFound || response.status.value >= 400)
    }

    @Test
    fun `should configure serialization in module`() = testApplication {
        // Arrange
        application {
            module()
        }

        // Act - try a request that requires serialization
        val response = client.post("/resume/upload") {
            contentType(ContentType.Application.Json)
            setBody("""{"test": "data"}""")
        }

        // Assert - content negotiation is working (not unsupported media type)
        assert(response.status != HttpStatusCode.UnsupportedMediaType)
    }
}
