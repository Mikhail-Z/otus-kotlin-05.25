import ai.snapmatch.api.v1.models.*
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ApiV1RequestMapperTest {

    @Test
    fun `should serialize ResumeUploadRequest to JSON`() {
        val request = ResumeUploadRequest(
            debug = Debug(
                mode = DebugMode.PROD,
                stub = DebugStubs.SUCCESS
            ),
            resume = ResumeUploadObject(
                id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000"),
                vacancyId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"),
                file = FileRequest(
                    contentB64 = "dGVzdCBjb250ZW50".toByteArray(),
                    fileName = "test-resume.pdf"
                )
            ),
            requestType = "resumeUpload"
        )

        val json = apiV1RequestSerialize(request)
        
        assertNotNull(json)
        assert(json.contains("resumeUpload"))
        assert(json.contains("test-resume.pdf"))
        assert(json.contains("550e8400-e29b-41d4-a716-446655440000"))
        assert(json.contains("PROD"))
        assert(json.contains("SUCCESS"))
    }

    @Test
    fun `should deserialize JSON to ResumeUploadRequest`() {
        val json = """
            {
                "debug": {
                    "mode": "PROD",
                    "stub": "SUCCESS"
                },
                "resume": {
                    "id": "550e8400-e29b-41d4-a716-446655440000",
                    "vacancyId": "550e8400-e29b-41d4-a716-446655440001",
                    "file": {
                        "contentB64": "dGVzdCBjb250ZW50",
                        "fileName": "test-resume.pdf"
                    }
                },
                "requestType": "resumeUpload"
            }
        """.trimIndent()

        val request: ResumeUploadRequest = apiV1RequestDeserialize(json)
        
        assertEquals("resumeUpload", request.requestType)
        assertEquals(DebugMode.PROD, request.debug.mode)
        assertEquals(DebugStubs.SUCCESS, request.debug.stub)
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"), request.resume.id)
        assertEquals(UUID.fromString("550e8400-e29b-41d4-a716-446655440001"), request.resume.vacancyId)
        assertEquals("test-resume.pdf", request.resume.file.fileName)
    }

    @Test
    fun `should handle serialization roundtrip for ResumeUploadRequest`() {
        val originalRequest = ResumeUploadRequest(
            debug = Debug(
                mode = DebugMode.TEST,
                stub = DebugStubs.BAD_ID
            ),
            resume = ResumeUploadObject(
                id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                vacancyId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                file = FileRequest(
                    contentB64 = "cm91bmR0cmlwIGNvbnRlbnQ=".toByteArray(),
                    fileName = "roundtrip-test.pdf"
                )
            ),
            requestType = "resumeUpload"
        )

        val json = apiV1RequestSerialize(originalRequest)
        val deserializedRequest: ResumeUploadRequest = apiV1RequestDeserialize(json)
        
        assertEquals(originalRequest.requestType, deserializedRequest.requestType)
        assertEquals(originalRequest.debug.mode, deserializedRequest.debug.mode)
        assertEquals(originalRequest.debug.stub, deserializedRequest.debug.stub)
        assertEquals(originalRequest.resume.id, deserializedRequest.resume.id)
        assertEquals(originalRequest.resume.vacancyId, deserializedRequest.resume.vacancyId)
        assertEquals(originalRequest.resume.file.fileName, deserializedRequest.resume.file.fileName)
    }
}