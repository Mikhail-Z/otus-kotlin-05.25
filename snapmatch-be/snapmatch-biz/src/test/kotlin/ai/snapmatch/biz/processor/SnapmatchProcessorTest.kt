package ai.snapmatch.biz.processor

import ai.snapmatch.app.common.api.IResumeService
import ai.snapmatch.biz.CorSettings
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.State
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class SnapmatchProcessorTest {

    @Test
    fun `should set resumeResponse from stub on UPLOAD_RESUME`() = runBlocking {
        // Arrange
        val mockResumeService = mockk<IResumeService>(relaxed = true)
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockResumeService
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.UPLOAD_RESUME)

        coEvery { mockResumeService.processResume(any()) } returns Unit

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assertNotNull(context.resumeResponse)
        coVerify(exactly = 1) { mockResumeService.processResume(any()) }
    }

    @Test
    fun `should call resumeService processResume on UPLOAD_RESUME`() = runBlocking {
        // Arrange
        val mockResumeService = mockk<IResumeService>(relaxed = true)
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockResumeService
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.UPLOAD_RESUME)

        var serviceInvoked = false
        coEvery { mockResumeService.processResume(any()) } answers {
            serviceInvoked = true
        }

        // Act
        processor.exec(context)

        // Assert
        assertEquals(true, serviceInvoked)
        assertEquals(State.FINISHING, context.state)
    }

    @Test
    fun `should return resumeResponse on GET_RESUME`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.GET_RESUME)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assertNotNull(context.resumeResponse)
    }

    @Test
    fun `should populate resumeResponse on DOWNLOAD_RESUME`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.DOWNLOAD_RESUME)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assertNotNull(context.resumeResponse)
    }

    @Test
    fun `should populate resumesResponse with list on GET_MY_RESUMES`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.GET_MY_RESUMES)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assert(context.resumesResponse.isNotEmpty()) { "resumesResponse should not be empty" }
    }

    @Test
    fun `should create vacancyResponse on CREATE_VACANCY`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.CREATE_VACANCY)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assertNotNull(context.vacancyResponse)
    }

    @Test
    fun `should return vacancyResponse on GET_VACANCY`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.GET_VACANCY)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assertNotNull(context.vacancyResponse)
    }

    @Test
    fun `should set vacancy to inactive on DELETE_VACANCY`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.DELETE_VACANCY)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assertNotNull(context.vacancyResponse)
        assertFalse(context.vacancyResponse.isActive, "Vacancy should be marked as inactive")
    }

    @Test
    fun `should populate vacanciesResponse list on SEARCH_VACANCIES`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.SEARCH_VACANCIES)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assert(context.vacanciesResponse.isNotEmpty()) { "vacanciesResponse should not be empty" }
    }

    @Test
    fun `should populate resumesResponse list on GET_VACANCY_RESUMES`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.GET_VACANCY_RESUMES)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FINISHING, context.state)
        assert(context.resumesResponse.isNotEmpty()) { "resumesResponse should not be empty" }
    }

    @Test
    fun `should add error on NONE command`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.NONE)

        // Act
        processor.exec(context)

        // Assert
        assertEquals(State.FAILING, context.state)
        assertEquals(1, context.errors.size)
    }

    @Test
    fun `should have proper error details on NONE command`() = runBlocking {
        // Arrange
        val corSettings = CorSettings(
            wsSessionRepo = mockk(relaxed = true),
            resumeService = mockk(relaxed = true)
        )
        val processor = SnapmatchProcessor(corSettings)
        val context = Context(command = Command.NONE)

        // Act
        processor.exec(context)

        // Assert
        val error = context.errors.first()
        assertEquals("unknown-command", error.code)
        assertEquals("validation", error.group)
        assertEquals("command", error.field)
        assertEquals("Unknown command", error.message)
    }
}
