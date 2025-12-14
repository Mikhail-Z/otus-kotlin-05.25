package ai.snapmatch.app.common

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.ISnapmatchProcessor
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.State
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ControllerHelperTest {

    @Test
    fun `should process successful request without errors`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
        }

        // Act
        val result = appSettings.processRequest(
            getRequest = {
                command = Command.GET_RESUME
            },
            toResponse = {
                "Success response"
            },
            clazz = String::class
        )

        // Assert
        assertEquals("Success response", result)
        coVerify(exactly = 1) { mockProcessor.exec(any()) }
    }

    @Test
    fun `should handle exceptions and set FAILING state`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        val testException = RuntimeException("Test exception")
        var capturedContext: Context? = null

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            capturedContext = ctx
        }

        // Act
        val result = appSettings.processRequest(
            getRequest = {
                command = Command.UPLOAD_RESUME
                throw testException
            },
            toResponse = {
                "Error response: ${errors.size} errors"
            },
            clazz = String::class
        )

        // Assert
        assertTrue(result.contains("1 errors"))
        assertEquals(State.FAILING, capturedContext?.state)
        assertEquals(1, capturedContext?.errors?.size)
        coVerify(exactly = 1) { mockProcessor.exec(any()) }
    }

    @Test
    fun `should execute processor on successful request flow`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var processorInvoked = false
        coEvery { mockProcessor.exec(any()) } answers {
            processorInvoked = true
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
        }

        // Act
        appSettings.processRequest(
            getRequest = {
                command = Command.CREATE_VACANCY
            },
            toResponse = {
                "Response"
            },
            clazz = String::class
        )

        // Assert
        assertTrue(processorInvoked)
        coVerify(exactly = 1) { mockProcessor.exec(any()) }
    }

    @Test
    fun `should convert exception to SnapmatchError`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var capturedContext: Context? = null
        coEvery { mockProcessor.exec(any()) } answers {
            capturedContext = firstArg<Context>()
        }

        val customException = IllegalArgumentException("Invalid argument")

        // Act
        appSettings.processRequest(
            getRequest = {
                throw customException
            },
            toResponse = {
                "Response"
            },
            clazz = String::class
        )

        // Assert
        assertEquals(1, capturedContext?.errors?.size)
        val error = capturedContext?.errors?.first()
        assertTrue(error?.message?.contains("Invalid argument") == true)
    }

    @Test
    fun `should execute processor even when toResponse fails`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        coEvery { mockProcessor.exec(any()) } answers {
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
        }

        // Act & Assert
        try {
            appSettings.processRequest(
                getRequest = {
                    command = Command.GET_RESUME
                },
                toResponse = {
                    throw RuntimeException("toResponse failed")
                },
                clazz = String::class
            )
        } catch (e: RuntimeException) {
            // Expected - toResponse throws exception after processor.exec
        }

        // Assert processor was called before toResponse exception
        coVerify(exactly = 2) { mockProcessor.exec(match { it.state == State.FINISHING }) }
    }

    @Test
    fun `should set default GET_RESUME command when NONE on error`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var capturedContext: Context? = null
        coEvery { mockProcessor.exec(any()) } answers {
            capturedContext = firstArg<Context>()
        }

        // Act
        appSettings.processRequest(
            getRequest = {
                // command defaults to NONE
                throw RuntimeException("Test error")
            },
            toResponse = {
                "Response"
            },
            clazz = String::class
        )

        // Assert
        assertEquals(Command.GET_RESUME, capturedContext?.command)
    }

    @Test
    fun `should preserve request data in context on error`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var capturedContext: Context? = null
        coEvery { mockProcessor.exec(any()) } answers {
            capturedContext = firstArg<Context>()
        }

        // Act
        appSettings.processRequest(
            getRequest = {
                command = Command.UPLOAD_RESUME
                throw RuntimeException("After setting command")
            },
            toResponse = {
                "Response"
            },
            clazz = String::class
        )

        // Assert
        assertEquals(State.FAILING, capturedContext?.state)
        assertEquals(1, capturedContext?.errors?.size)
    }

    @Test
    fun `should call toResponse after processor exec in success path`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var processorCalled = false
        var toResponseCalled = false

        coEvery { mockProcessor.exec(any()) } answers {
            processorCalled = true
            val ctx = firstArg<Context>()
            ctx.state = State.FINISHING
        }

        // Act
        appSettings.processRequest(
            getRequest = {
                command = Command.GET_VACANCY
            },
            toResponse = {
                assertTrue(processorCalled, "Processor should be called before toResponse")
                toResponseCalled = true
                "Response"
            },
            clazz = String::class
        )

        // Assert
        assertTrue(processorCalled)
        assertTrue(toResponseCalled)
    }

    @Test
    fun `should call toResponse after processor exec in error path`() = runBlocking {
        // Arrange
        val mockProcessor = mockk<ISnapmatchProcessor>()
        val appSettings = mockk<IAppSettings> {
            every { processor } returns mockProcessor
        }

        var processorCalled = false
        var toResponseCalled = false

        coEvery { mockProcessor.exec(any()) } answers {
            processorCalled = true
        }

        // Act
        appSettings.processRequest(
            getRequest = {
                throw RuntimeException("Test error")
            },
            toResponse = {
                assertTrue(processorCalled, "Processor should be called before toResponse even in error path")
                toResponseCalled = true
                "Error Response"
            },
            clazz = String::class
        )

        // Assert
        assertTrue(processorCalled)
        assertTrue(toResponseCalled)
    }
}
