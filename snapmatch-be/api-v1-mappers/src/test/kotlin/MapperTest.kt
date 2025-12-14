import ai.snapmatch.api.v1.models.*
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.*
import ai.snapmatch.common.stubs.DebugStubs
import ai.snapmatch.stubs.ResumeAnalysisStub
import ai.snapmatch.stubs.ResumeStub
import mappers.input.fromRequest
import mappers.output.toResumeUploadResponse
import mappers.output.toResumeGetResponse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MapperTest {

    @Test
    fun `map upload request to context`() {
        // Arrange - используем UUID из стабов
        val resumeId = java.util.UUID.fromString("660e8400-e29b-41d4-a716-446655440001")
        val vacancyId = java.util.UUID.fromString("550e8400-e29b-41d4-a716-446655440001")
        val req = ResumeUploadRequestDto(
            debug = DebugDto(
                mode = DebugModeDto.STUB,
                stub = DebugStubsDto.SUCCESS
            ),
            resume = ResumeUploadObjectDto(
                id = resumeId,
                vacancyId = vacancyId,
                file = FileRequestDto(
                    contentB64 = "VGhpcyBpcyBhIHRlc3QgcmVzdW1lIGZpbGUu",
                    fileName = "test-resume.pdf",
                )
            )
        )

        val context = Context()

        // Act
        context.fromRequest(req)

        // Assert
        assertEquals(Command.UPLOAD_RESUME, context.command)
        assertEquals(WorkMode.STUB, context.workMode)
        assertEquals(DebugStubs.SUCCESS, context.stubCase)
        assertEquals(resumeId.toString(), context.resumeRequest.id.asString())
        assertEquals(vacancyId.toString(), context.resumeRequest.vacancyId.asString())
        assertEquals("test-resume.pdf", context.resumeRequest.fileName)
    }

    @Test
    fun `map context to get resume response when success`() {
        // Arrange
        val resumeWithAnalysis = ResumeStub.get().copy(
            analysis = ResumeAnalysisStub.get()
        )

        val context = Context(
            command = Command.GET_RESUME,
            state = State.FINISHING,
            resumeResponse = resumeWithAnalysis
        )

        // Act
        val response = context.toResumeGetResponse()

        // Assert
        assertEquals("resumeGet", response.responseType)
        assertEquals(ResponseResultDto.SUCCESS, response.result)
        assertNotNull(response.resume)
        
        // Assert Resume fields
        assertEquals(resumeWithAnalysis.fileName, response.resume?.fileName)
        assertEquals(resumeWithAnalysis.fileKey, response.resume?.fileKey)
        assertEquals(resumeWithAnalysis.fileSize, response.resume?.fileSize)
        assertEquals(ResumeProcessingStatusDto.ACCEPTED, response.resume?.status)
        
        // Assert ResumeAnalysis
        assertNotNull(response.resume?.analysis)
        assertEquals(resumeWithAnalysis.analysis?.score, response.resume?.analysis?.score)
        assertEquals(LLMProviderDto.GIGACHAT, response.resume?.analysis?.llmProvider)
        assertEquals(resumeWithAnalysis.analysis?.llmModel, response.resume?.analysis?.llmModel)
    }

    @Test
    fun `map context to upload resume response when error`() {
        // Arrange
        val context = Context(
            command = Command.UPLOAD_RESUME,
            state = State.FAILING,
            errors = mutableListOf(
                Error(
                    code = "validation",
                    group = "request", 
                    field = "fileName",
                    message = "File name is required"
                ),
                Error(
                    code = "size",
                    group = "business",
                    field = "fileSize", 
                    message = "File too large"
                )
            )
        )

        // Act
        val response = context.toResumeUploadResponse()

        // Assert
        assertEquals("resumeUpload", response.responseType)
        assertEquals(ResponseResultDto.ERROR, response.result)
        
        // Assert errors
        assertEquals(2, response.errors?.size)
        val firstError = response.errors?.find { it.code == "validation" }
        assertEquals("request", firstError?.group)
        assertEquals("fileName", firstError?.field)
        assertEquals("File name is required", firstError?.message)
        
        val secondError = response.errors?.find { it.code == "size" }
        assertEquals("business", secondError?.group)
        assertEquals("fileSize", secondError?.field)
        assertEquals("File too large", secondError?.message)
    }
}