package ai.snapmatch.api.v1.jackson

import ai.snapmatch.api.v1.models.*
import kotlinx.datetime.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiV1ResponseMapperTest {

    @Test
    fun `should serialize ResumeUploadResponse to JSON`() {
        val response = ResumeUploadResponseDto(
            responseType = "uploadResume",
            result = ResponseResultDto.SUCCESS,
            errors = emptyList(),
            resume = ResumeResponseObjectDto(
                id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                vacancyId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440002"),
                fileName = "test-resume.pdf",
                fileKey = "uploads/550e8400-e29b-41d4-a716-446655440000.pdf",
                fileSize = 1024,
                uploadedAt = Instant.parse("2023-12-01T10:30:00Z"),
                status = ResumeProcessingStatusDto.UPLOADED
            )
        )

        val json = apiV1ResponseSerialize(response)
        
        assertNotNull(json)
        assert(json.contains("uploadResume"))
        assert(json.contains("success"))
        assert(json.contains("test-resume.pdf"))
        assert(json.contains("550e8400-e29b-41d4-a716-446655440000"))
        assert(json.contains("UPLOADED"))
    }

    @Test
    fun `should deserialize JSON to ResumeUploadResponse`() {
        val json = """
            {
                "responseType": "uploadResume",
                "result": "success",
                "errors": [],
                "resume": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "vacancyId": "550e8400-e29b-41d4-a716-446655440001",
                    "userId": "550e8400-e29b-41d4-a716-446655440002",
                    "fileName": "test-resume.pdf",
                    "fileKey": "uploads/550e8400-e29b-41d4-a716-446655440000.pdf",
                    "fileSize": 1024,
                    "uploadedAt": "2023-12-01T10:30:00Z",
                    "status": "UPLOADED"
                }
            }
        """.trimIndent()

        val response: ResumeUploadResponseDto = apiV1ResponseDeserialize(json)
        
        assertEquals("uploadResume", response.responseType)
        assertEquals(ResponseResultDto.SUCCESS, response.result)
        assertEquals(0, response.errors?.size)
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), response.resume?.id)
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), response.resume?.vacancyId)
        assertEquals("test-resume.pdf", response.resume?.fileName)
        assertEquals(ResumeProcessingStatusDto.UPLOADED, response.resume?.status)
    }

    @Test
    fun `should handle serialization roundtrip for ResumeUploadResponse`() {
        val originalResponse = ResumeUploadResponseDto(
            responseType = "uploadResume",
            result = ResponseResultDto.ERROR,
            errors = listOf(
                ErrorDto(
                    code = "VALIDATION_ERROR",
                    group = "request",
                    field = "file",
                    message = "File is too large"
                )
            ),
            resume = null
        )

        val json = apiV1ResponseSerialize(originalResponse)
        val deserializedResponse: ResumeUploadResponseDto = apiV1ResponseDeserialize(json)
        
        assertEquals(originalResponse.responseType, deserializedResponse.responseType)
        assertEquals(originalResponse.result, deserializedResponse.result)
        assertEquals(originalResponse.errors?.size, deserializedResponse.errors?.size)
        assertEquals(originalResponse.errors?.first()?.code, deserializedResponse.errors?.first()?.code)
        assertEquals(originalResponse.resume, deserializedResponse.resume)
    }

    @Test
    fun `should serialize response with processing status`() {
        val response = ResumeUploadResponseDto(
            responseType = "uploadResume",
            result = ResponseResultDto.SUCCESS,
            errors = null,
            resume = ResumeResponseObjectDto(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                vacancyId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174002"),
                fileName = "processed-resume.pdf",
                fileKey = "uploads/processed.pdf",
                fileSize = 2048,
                uploadedAt = Instant.parse("2023-12-01T12:00:00Z"),
                status = ResumeProcessingStatusDto.PROCESSING
            )
        )

        val json = apiV1ResponseSerialize(response)
        val deserializedResponse: ResumeUploadResponseDto = apiV1ResponseDeserialize(json)
        
        assertEquals(ResumeProcessingStatusDto.PROCESSING, deserializedResponse.resume?.status)
        assertEquals("processed-resume.pdf", deserializedResponse.resume?.fileName)
        assertEquals(2048, deserializedResponse.resume?.fileSize)
    }
}