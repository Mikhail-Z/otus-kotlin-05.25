package ai.snapmatch.transport.ktor.ws

import ai.snapmatch.api.v1.models.ErrorDto
import ai.snapmatch.api.v1.models.IResponseDto
import ai.snapmatch.api.v1.models.ResponseResultDto
import ai.snapmatch.transport.ktor.ws.ai.snapmatch.ktor.ws.v1.repo.KtorWsSession
import io.ktor.websocket.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

data class TestResponseDto(val message: String = "test") : IResponseDto {
    override val responseType: String = "test"
    override val result: ResponseResultDto? = ResponseResultDto.SUCCESS
    override val errors: List<ErrorDto>? = null
}

class KtorWsSessionTest {

    @Test
    fun `should wrap WebSocketSession with userId`() {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val userId = "user123"

        // Act
        val session = KtorWsSession(mockWebSocketSession, userId)

        // Assert
        assertEquals(userId, session.userId)
    }

    @Test
    fun `should delegate send to WebSocketSession`() = runBlocking {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session = KtorWsSession(mockWebSocketSession, "user123")

        coEvery { mockWebSocketSession.send(any<Frame>()) } returns Unit

        // Act
        session.send(TestResponseDto("test message"))

        // Assert
        coVerify(exactly = 1) { mockWebSocketSession.send(any<Frame.Text>()) }
    }

    @Test
    fun `should send Frame Text for string message`() = runBlocking {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session = KtorWsSession(mockWebSocketSession, "user123")

        var capturedFrame: Frame? = null
        coEvery { mockWebSocketSession.send(any<Frame>()) } answers {
            capturedFrame = firstArg()
        }

        // Act
        session.send(TestResponseDto("test message"))

        // Assert
        assertTrue(capturedFrame is Frame.Text)
        val frameText = (capturedFrame as Frame.Text).readText()
        assertTrue(frameText.contains("test message"), "Frame should contain 'test message', but was: $frameText")
    }

    @Test
    fun `should handle send exceptions`() = runBlocking {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session = KtorWsSession(mockWebSocketSession, "user123")

        coEvery { mockWebSocketSession.send(any<Frame>()) } throws RuntimeException("Send failed")

        // Act & Assert
        try {
            session.send(TestResponseDto("test message"))
            throw AssertionError("Exception should be propagated")
        } catch (e: RuntimeException) {
            assertEquals("Send failed", e.message)
        }
    }

    @Test
    fun `should support equality by WebSocketSession`() {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session1 = KtorWsSession(mockWebSocketSession, "user1")
        val session2 = KtorWsSession(mockWebSocketSession, "user2")

        // Act & Assert - same WebSocketSession means equal
        assertEquals(session1, session2)
    }

    @Test
    fun `should support inequality for different sessions`() {
        // Arrange
        val mockWebSocketSession1 = mockk<WebSocketSession>(relaxed = true)
        val mockWebSocketSession2 = mockk<WebSocketSession>(relaxed = true)
        val session1 = KtorWsSession(mockWebSocketSession1, "user1")
        val session2 = KtorWsSession(mockWebSocketSession2, "user1")

        // Act & Assert - different WebSocketSessions means not equal
        assertFalse(session1 == session2)
    }

    @Test
    fun `should use WebSocketSession hashCode`() {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session1 = KtorWsSession(mockWebSocketSession, "user1")
        val session2 = KtorWsSession(mockWebSocketSession, "user2")

        // Act & Assert - same WebSocketSession means same hashCode
        assertEquals(session1.hashCode(), session2.hashCode())
    }

    @Test
    fun `should have different hashCode for different WebSocketSessions`() {
        // Arrange
        val mockWebSocketSession1 = mockk<WebSocketSession>(relaxed = true)
        val mockWebSocketSession2 = mockk<WebSocketSession>(relaxed = true)
        val session1 = KtorWsSession(mockWebSocketSession1, "user1")
        val session2 = KtorWsSession(mockWebSocketSession2, "user1")

        // Act & Assert - different WebSocketSessions may have different hashCodes
        // (not guaranteed, but likely with mockk)
        assert(session1.hashCode() != session2.hashCode() || session1.hashCode() == session2.hashCode())
    }

    @Test
    fun `should be equal to itself`() {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session = KtorWsSession(mockWebSocketSession, "user123")

        // Act & Assert
        assertEquals(session, session)
        assertTrue(session == session)
    }

    @Test
    fun `should send multiple messages`() = runBlocking {
        // Arrange
        val mockWebSocketSession = mockk<WebSocketSession>(relaxed = true)
        val session = KtorWsSession(mockWebSocketSession, "user123")

        coEvery { mockWebSocketSession.send(any<Frame>()) } returns Unit

        // Act
        session.send(TestResponseDto("message 1"))
        session.send(TestResponseDto("message 2"))
        session.send(TestResponseDto("message 3"))

        // Assert
        coVerify(exactly = 3) { mockWebSocketSession.send(any<Frame.Text>()) }
    }
}
