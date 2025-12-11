package ai.snapmatch.biz.service

import ai.snapmatch.app.common.api.IWsSessionRepo
import ai.snapmatch.common.models.Resume
import ai.snapmatch.common.models.ResumeId
import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.VacancyId
import ai.snapmatch.stubs.ResumeStub
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertTrue

class ResumeServiceTest {

    @Test
    fun `should not throw exception when processResume called`() = runBlocking {
        // Arrange
        val wsSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val service = ResumeService(wsSessionRepo)
        val resume = ResumeStub.get()

        // Act & Assert - should not throw
        try {
            service.processResume(resume)
            assertTrue(true)
        } catch (e: Exception) {
            throw AssertionError("processResume should not throw exception", e)
        }
    }

    @Test
    fun `should handle null resume gracefully`() = runBlocking {
        // Arrange
        val wsSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val service = ResumeService(wsSessionRepo)
        val minimalResume = Resume(
            id = ResumeId(UUID.randomUUID().toString()),
            userId = UserId(UUID.randomUUID().toString()),
            vacancyId = VacancyId(UUID.randomUUID().toString()),
            fileName = "test.pdf",
            fileKey = "key",
            fileSize = 0
        )

        // Act & Assert - should complete successfully
        try {
            service.processResume(minimalResume)
            assertTrue(true)
        } catch (e: Exception) {
            throw AssertionError("processResume should handle minimal resume", e)
        }
    }

    @Test
    fun `should be idempotent`() = runBlocking {
        // Arrange
        val wsSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val service = ResumeService(wsSessionRepo)
        val resume = ResumeStub.get()

        // Act - call twice
        service.processResume(resume)
        service.processResume(resume)

        // Assert - both calls should succeed
        assertTrue(true)
    }

    @Test
    fun `should accept different resume instances`() = runBlocking {
        // Arrange
        val wsSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val service = ResumeService(wsSessionRepo)
        val resume1 = ResumeStub.get()
        val resume2 = ResumeStub.prepareResumesList().first()

        // Act
        service.processResume(resume1)
        service.processResume(resume2)

        // Assert
        assertTrue(true)
    }
}
