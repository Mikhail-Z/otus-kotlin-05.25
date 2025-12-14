package ai.snapmatch.transport.ktor.ws

import ai.snapmatch.api.v1.models.ResumeAnalysisDetailsDto
import ai.snapmatch.app.common.api.IWsSession
import ai.snapmatch.transport.ktor.ws.ai.snapmatch.ktor.ws.v1.repo.KtorWsSessionRepo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class KtorWsSessionRepoTest {

    @Test
    fun `should add session to repository`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session = mockk<IWsSession>(relaxed = true)
        every { session.userId } returns "user123"

        // Act
        repo.add(session)

        // Assert
        val sessions = repo.getSessions("user123")
        assertEquals(1, sessions.size)
        assertTrue(sessions.contains(session))
    }

    @Test
    fun `should support multiple sessions per user`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session1 = mockk<IWsSession>(relaxed = true)
        val session2 = mockk<IWsSession>(relaxed = true)
        every { session1.userId } returns "user123"
        every { session2.userId } returns "user123"

        // Act
        repo.add(session1)
        repo.add(session2)

        // Assert
        val sessions = repo.getSessions("user123")
        assertEquals(2, sessions.size)
        assertTrue(sessions.contains(session1))
        assertTrue(sessions.contains(session2))
    }

    @Test
    fun `should support multiple users`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session1 = mockk<IWsSession>(relaxed = true)
        val session2 = mockk<IWsSession>(relaxed = true)
        every { session1.userId } returns "user1"
        every { session2.userId } returns "user2"

        // Act
        repo.add(session1)
        repo.add(session2)

        // Assert
        assertEquals(1, repo.getSessions("user1").size)
        assertEquals(1, repo.getSessions("user2").size)
        assertTrue(repo.getSessions("user1").contains(session1))
        assertTrue(repo.getSessions("user2").contains(session2))
    }

    @Test
    fun `should remove session from repository`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session = mockk<IWsSession>(relaxed = true)
        every { session.userId } returns "user123"
        repo.add(session)

        // Act
        repo.remove(session)

        // Assert
        val sessions = repo.getSessions("user123")
        assertEquals(0, sessions.size)
    }

    @Test
    fun `should remove user key when last session removed`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session = mockk<IWsSession>(relaxed = true)
        every { session.userId } returns "user123"
        repo.add(session)

        // Act
        repo.remove(session)

        // Assert
        val sessions = repo.getSessions("user123")
        assertTrue(sessions.isEmpty(), "Sessions should be empty after removing last session")
    }

    @Test
    fun `should support removing one of multiple sessions`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session1 = mockk<IWsSession>(relaxed = true)
        val session2 = mockk<IWsSession>(relaxed = true)
        every { session1.userId } returns "user123"
        every { session2.userId } returns "user123"
        repo.add(session1)
        repo.add(session2)

        // Act
        repo.remove(session1)

        // Assert
        val sessions = repo.getSessions("user123")
        assertEquals(1, sessions.size)
        assertTrue(sessions.contains(session2))
        assertTrue(!sessions.contains(session1))
    }

    @Test
    fun `should return empty list for unknown user`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()

        // Act
        val sessions = repo.getSessions("unknown-user")

        // Assert
        assertTrue(sessions.isEmpty())
    }

    @Test
    fun `should return list of sessions for user`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session1 = mockk<IWsSession>(relaxed = true)
        val session2 = mockk<IWsSession>(relaxed = true)
        every { session1.userId } returns "user123"
        every { session2.userId } returns "user123"
        repo.add(session1)
        repo.add(session2)

        // Act
        val sessions = repo.getSessions("user123")

        // Assert
        assertEquals(2, sessions.size)
    }

    @Test
    fun `should send message to all user sessions`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session1 = mockk<IWsSession>(relaxed = true)
        val session2 = mockk<IWsSession>(relaxed = true)
        every { session1.userId } returns "user123"
        every { session2.userId } returns "user123"
        coEvery { session1.send<ResumeAnalysisDetailsDto>(any()) } returns Unit
        coEvery { session2.send<ResumeAnalysisDetailsDto>(any()) } returns Unit
        repo.add(session1)
        repo.add(session2)

        // Act
        repo.sendToUser("user123", "Test message")

        // Assert
        coVerify(exactly = 1) { session1.send("Test message") }
        coVerify(exactly = 1) { session2.send("Test message") }
    }

    @Test
    fun `should handle ClosedSendChannelException`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session = mockk<IWsSession>(relaxed = true)
        every { session.userId } returns "user123"
        coEvery { session.send<ResumeAnalysisDetailsDto>(any()) } throws ClosedSendChannelException("Channel closed")
        repo.add(session)

        // Act
        repo.sendToUser("user123", "Test message")

        // Assert - session should be removed after exception
        val sessions = repo.getSessions("user123")
        assertEquals(0, sessions.size)
    }

    @Test
    fun `should handle other exceptions`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session = mockk<IWsSession>(relaxed = true)
        every { session.userId } returns "user123"
        coEvery { session.send<ResumeAnalysisDetailsDto>(any()) } throws RuntimeException("Send failed")
        repo.add(session)

        // Act
        repo.sendToUser("user123", "Test message")

        // Assert - session should be removed after exception
        val sessions = repo.getSessions("user123")
        assertEquals(0, sessions.size)
    }

    @Test
    fun `should remove dead sessions after sending`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session1 = mockk<IWsSession>(relaxed = true)
        val session2 = mockk<IWsSession>(relaxed = true)
        val session3 = mockk<IWsSession>(relaxed = true)
        every { session1.userId } returns "user123"
        every { session2.userId } returns "user123"
        every { session3.userId } returns "user123"

        // session2 throws exception, others succeed
        coEvery { session1.send<ResumeAnalysisDetailsDto>(any()) } returns Unit
        coEvery { session2.send<ResumeAnalysisDetailsDto>(any()) } throws ClosedSendChannelException("Dead")
        coEvery { session3.send<ResumeAnalysisDetailsDto>(any()) } returns Unit

        repo.add(session1)
        repo.add(session2)
        repo.add(session3)

        // Act
        repo.sendToUser("user123", "Test message")

        // Assert - only session2 should be removed
        val sessions = repo.getSessions("user123")
        assertEquals(2, sessions.size)
        assertTrue(sessions.contains(session1))
        assertTrue(!sessions.contains(session2))
        assertTrue(sessions.contains(session3))
    }

    @Test
    fun `should return silently if user has no sessions`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()

        // Act & Assert - should not throw exception
        repo.sendToUser("nonexistent-user", "Test message")

        // If we get here, test passed - no exception was thrown
        assertTrue(true)
    }

    @Test
    fun `should add same session only once per user`() = runBlocking {
        // Arrange
        val repo = KtorWsSessionRepo()
        val session = mockk<IWsSession>(relaxed = true)
        every { session.userId } returns "user123"

        // Act - add same session twice
        repo.add(session)
        repo.add(session)

        // Assert - should only be added once (Set behavior)
        val sessions = repo.getSessions("user123")
        assertEquals(1, sessions.size)
    }
}
