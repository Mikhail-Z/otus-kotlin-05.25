package ai.snapmatch.api.v1.jackson

import ai.snapmatch.api.v1.models.*
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiV1RequestMapperTest {

    @Test
    fun `should serialize ResumeUploadRequest to JSON`() {
        val request = ResumeUploadRequestDto(
            debug = DebugDto(
                mode = DebugModeDto.PROD,
                stub = DebugStubsDto.SUCCESS
            ),
            resume = ResumeUploadObjectDto(
                id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                vacancyId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                file = FileRequestDto(
                    contentB64 = "dGVzdCBjb250ZW50",
                    fileName = "test-resume.pdf"
                )
            ),
            requestType = "uploadResume"
        )

        val json = apiV1RequestSerialize(request)

        assertNotNull(json)
        assert(json.contains("uploadResume"))
        assert(json.contains("test-resume.pdf"))
        assert(json.contains("550e8400-e29b-41d4-a716-446655440000"))
        assert(json.contains("prod"))
        assert(json.contains("success"))
    }

    @Test
    fun `should deserialize JSON to ResumeUploadRequest`() {
        val json = """
            {
                "debug": {
                    "mode": "prod",
                    "stub": "success"
                },
                "resume": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "vacancyId": "550e8400-e29b-41d4-a716-446655440001",
                    "file": {
                        "contentB64": "dGVzdCBjb250ZW50",
                        "fileName": "test-resume.pdf"
                    }
                },
                "requestType": "uploadResume"
            }
        """.trimIndent()

        val request: ResumeUploadRequestDto = apiV1RequestDeserialize(json)

        assertEquals("uploadResume", request.requestType)
        assertEquals(DebugModeDto.PROD, request.debug?.mode)
        assertEquals(DebugStubsDto.SUCCESS, request.debug?.stub)
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), request.resume?.id)
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), request.resume?.vacancyId)
        assertEquals("test-resume.pdf", request.resume?.file?.fileName)
    }

    @Test
    fun `should handle serialization roundtrip for ResumeUploadRequest`() {
        val originalRequest = ResumeUploadRequestDto(
            debug = DebugDto(
                mode = DebugModeDto.TEST,
                stub = DebugStubsDto.BAD_ID
            ),
            resume = ResumeUploadObjectDto(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                vacancyId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                file = FileRequestDto(
                    contentB64 = "cm91bmR0cmlwIGNvbnRlbnQ=",
                    fileName = "roundtrip-test.pdf"
                )
            ),
            requestType = "uploadResume"
        )

        val json = apiV1RequestSerialize(originalRequest)
        val deserializedRequest: ResumeUploadRequestDto = apiV1RequestDeserialize(json)

        assertEquals(originalRequest.requestType, deserializedRequest.requestType)
        assertEquals(originalRequest.debug?.mode, deserializedRequest.debug?.mode)
        assertEquals(originalRequest.debug?.stub, deserializedRequest.debug?.stub)
        assertEquals(originalRequest.resume?.id, deserializedRequest.resume?.id)
        assertEquals(originalRequest.resume?.vacancyId, deserializedRequest.resume?.vacancyId)
        assertEquals(originalRequest.resume?.file?.fileName, deserializedRequest.resume?.file?.fileName)
    }
}