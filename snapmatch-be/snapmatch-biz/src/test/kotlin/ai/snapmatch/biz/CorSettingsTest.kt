package ai.snapmatch.biz

import ai.snapmatch.app.common.api.IResumeService
import ai.snapmatch.app.common.api.IWsSessionRepo
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CorSettingsTest {

    @Test
    fun `should create instance with dependencies`() {
        // Arrange
        val mockWsSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val mockResumeService = mockk<IResumeService>(relaxed = true)

        // Act
        val corSettings = CorSettings(
            wsSessionRepo = mockWsSessionRepo,
            resumeService = mockResumeService
        )

        // Assert
        assertEquals(mockWsSessionRepo, corSettings.wsSessionRepo)
        assertEquals(mockResumeService, corSettings.resumeService)
    }

    @Test
    fun `should have NONE companion instance`() {
        // Act
        val noneSettings = CorSettings.NONE

        // Assert
        assertNotNull(noneSettings)
        assertNotNull(noneSettings.wsSessionRepo)
        assertNotNull(noneSettings.resumeService)
    }

    @Test
    fun `should preserve dependency references`() {
        // Arrange
        val mockWsSessionRepo = mockk<IWsSessionRepo>(relaxed = true)
        val mockResumeService = mockk<IResumeService>(relaxed = true)

        // Act
        val corSettings = CorSettings(
            wsSessionRepo = mockWsSessionRepo,
            resumeService = mockResumeService
        )

        // Assert - same object references
        assert(mockWsSessionRepo === corSettings.wsSessionRepo)
        assert(mockResumeService === corSettings.resumeService)
    }

    @Test
    fun `should be a data class with copy functionality`() {
        // Arrange
        val mockWsSessionRepo1 = mockk<IWsSessionRepo>(relaxed = true)
        val mockWsSessionRepo2 = mockk<IWsSessionRepo>(relaxed = true)
        val mockResumeService = mockk<IResumeService>(relaxed = true)

        val original = CorSettings(
            wsSessionRepo = mockWsSessionRepo1,
            resumeService = mockResumeService
        )

        // Act
        val copied = original.copy(wsSessionRepo = mockWsSessionRepo2)

        // Assert
        assertEquals(mockWsSessionRepo2, copied.wsSessionRepo)
        assertEquals(mockResumeService, copied.resumeService)
    }
}
