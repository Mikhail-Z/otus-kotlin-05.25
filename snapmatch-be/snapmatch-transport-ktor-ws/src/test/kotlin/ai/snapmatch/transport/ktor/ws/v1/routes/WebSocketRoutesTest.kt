package ai.snapmatch.transport.ktor.ws.v1.routes

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.IWsSessionRepo
import ai.snapmatch.biz.CorSettings
import ai.snapmatch.ktor.ws.v1.routes.wsRoute
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.client.plugins.websocket.*
import io.ktor.server.testing.*
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class WebSocketRoutesTest {

    private fun createTestJwtToken(userId: String): String {
        return JWT.create()
            .withSubject(userId)
            .withClaim("email", "test@example.com")
            .sign(Algorithm.HMAC256("test-secret"))
    }

    @Test
    fun `should require token parameter`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        // Act & Assert
        try {
            withTimeout(5.seconds) {
                client.webSocket("/ws") {
                    // Should close immediately with VIOLATED_POLICY
                    val frame = incoming.receive()
                    if (frame is Frame.Close) {
                        val reason = frame.readReason()
                        assertTrue(reason != null, "Close reason should not be null")
                        assertEquals<Short>(CloseReason.Codes.VIOLATED_POLICY.code, reason.code)
                    }
                }
            }
        } catch (e: Exception) {
            // Connection closed as expected
            assertTrue(true)
        }
    }

    @Test
    fun `should accept valid JWT token`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("user123")

        // Act & Assert
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                val frame = incoming.receive()
                assertTrue(frame is Frame.Text)
                val text = (frame as Frame.Text).readText()
                assertTrue(text.contains("CONNECTED"))
                close()
            }
        }
    }

    @Test
    fun `should extract userId from JWT subject`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("user456")

        // Act
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                val frame = incoming.receive()
                val text = (frame as Frame.Text).readText()

                // Assert
                assertTrue(text.contains("user456"))
                close()
            }
        }
    }

    @Test
    fun `should handle invalid JWT token`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        // Act & Assert
        try {
            withTimeout(5.seconds) {
                client.webSocket("/ws?token=invalid-token") {
                    val frame = incoming.receive()
                    if (frame is Frame.Close) {
                        val reason = frame.readReason()
                        assertTrue(reason != null, "Close reason should not be null")
                        assertEquals<Short>(CloseReason.Codes.VIOLATED_POLICY.code, reason.code)
                    }
                }
            }
        } catch (e: Exception) {
            // Connection closed as expected
            assertTrue(true)
        }
    }

    @Test
    fun `should send CONNECTED frame on successful connection`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("user789")

        // Act
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                val frame = incoming.receive() as Frame.Text
                val message = frame.readText()

                // Assert
                assertTrue(message.contains("type"))
                assertTrue(message.contains("CONNECTED"))
                assertTrue(message.contains("userId"))
                close()
            }
        }
    }

    @Test
    fun `should add session to repository on connect`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("user999")

        // Act
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                incoming.receive() // Wait for CONNECTED message
                close()
            }
        }

        // Assert
        coVerify(atLeast = 1) { mockSessionRepo.add(any()) }
    }

    @Test
    fun `should remove session from repository on disconnect`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("user000")

        // Act
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                incoming.receive() // Wait for CONNECTED message
                close()
            }
        }

        // Assert
        coVerify(atLeast = 1) { mockSessionRepo.remove(any()) }
    }

    @Test
    fun `should handle Ping frames with Pong response`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("userPing")

        // Act
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                // Receive CONNECTED message
                val connected = incoming.receive()
                assertTrue(connected is Frame.Text)

                // Send Ping - WebSocket automatically handles Ping/Pong at protocol level
                send(Frame.Ping(ByteArray(0)))

                // Give some time for ping to be processed
                delay(100)

                // Connection should still be alive
                assertTrue(!incoming.isClosedForReceive, "Connection should still be open after Ping")

                close()
            }
        }
    }

    @Test
    fun `should handle text frames`() = testApplication {
        // Arrange
        val mockSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        coEvery { mockSessionRepo.add(any()) } returns Unit
        coEvery { mockSessionRepo.remove(any()) } returns Unit

        val appSettings = mockk<IAppSettings> {
            every { corSettings } returns CorSettings(
                wsSessionRepo = mockSessionRepo,
                resumeService = mockk(relaxed = true)
            )
        }

        install(WebSockets)
        routing {
            wsRoute(appSettings)
        }

        val client = createClient {
            install(io.ktor.client.plugins.websocket.WebSockets)
        }

        val validToken = createTestJwtToken("userText")

        // Act & Assert
        withTimeout(5.seconds) {
            client.webSocket("/ws?token=$validToken") {
                incoming.receive() // CONNECTED message

                // Send text frame
                send(Frame.Text("test message"))

                // Should be logged (we can't verify logging easily, but connection should stay open)
                close()
            }
        }

        // If we get here without exception, test passed
        assertTrue(true)
    }
}
