package ai.snapmatch.stubs

import ai.snapmatch.common.models.Resume
import ai.snapmatch.common.models.ResumeId
import ai.snapmatch.common.models.ResumeProcessingStatus
import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.VacancyId
import kotlinx.datetime.Instant
import java.util.Base64

object ResumeStub {
    fun get(): Resume = RESUME_KOTLIN_DEV.copy()

    fun prepareResult(block: Resume.() -> Unit): Resume = get().apply(block)

    fun prepareResumesList(vacancyId: String = "550e8400-e29b-41d4-a716-446655440001") = listOf(
        resumeKotlin("660e8400-e29b-41d4-a716-446655440011", vacancyId),
        resumeJava("660e8400-e29b-41d4-a716-446655440012", vacancyId),
        resumePython("660e8400-e29b-41d4-a716-446655440013", vacancyId),
    )

    fun prepareMyResumesList(userId: String = "770e8400-e29b-41d4-a716-446655440001") = listOf(
        resumeKotlin("660e8400-e29b-41d4-a716-446655440021", "550e8400-e29b-41d4-a716-446655440001").copy(userId = UserId(
            userId
        )
        ),
        resumeJava("660e8400-e29b-41d4-a716-446655440022", "550e8400-e29b-41d4-a716-446655440002").copy(userId = UserId(
            userId
        )
        ),
        resumePython("660e8400-e29b-41d4-a716-446655440023", "550e8400-e29b-41d4-a716-446655440001").copy(userId = UserId(
            userId
        )
        ),
    )

    private fun resumeKotlin(id: String, vacancyId: String) =
        resume(RESUME_KOTLIN_DEV, id = id, vacancyId = vacancyId)

    private fun resumeJava(id: String, vacancyId: String) =
        resume(RESUME_JAVA_DEV, id = id, vacancyId = vacancyId)

    private fun resumePython(id: String, vacancyId: String) =
        resume(RESUME_PYTHON_DEV, id = id, vacancyId = vacancyId)

    private fun resume(base: Resume, id: String, vacancyId: String) = base.copy(
        id = ResumeId(id),
        vacancyId = VacancyId(vacancyId),
        fileName = "${base.fileName.substringBefore('.')}_$id.pdf",
        fileKey = "uploads/${id}/${base.fileName.substringBefore('.')}_$id.pdf",
    )

    private val RESUME_KOTLIN_DEV = Resume(
        id = ResumeId("660e8400-e29b-41d4-a716-446655440001"),
        vacancyId = VacancyId("550e8400-e29b-41d4-a716-446655440001"),
        userId = UserId("770e8400-e29b-41d4-a716-446655440001"), // Пользователь 1
        fileName = "kotlin_developer_resume.pdf",
        fileKey = "uploads/660e8400-e29b-41d4-a716-446655440001/kotlin_developer_resume.pdf",
        fileSize = 1024576, // 1MB
        uploadedAt = Instant.parse("2023-12-01T10:30:00Z"),
        status = ResumeProcessingStatus.ACCEPTED,
        analysis = ResumeAnalysisStub.KOTLIN_ANALYSIS
    )

    private val RESUME_JAVA_DEV = Resume(
        id = ResumeId("660e8400-e29b-41d4-a716-446655440002"),
        vacancyId = VacancyId("550e8400-e29b-41d4-a716-446655440002"),
        userId = UserId("770e8400-e29b-41d4-a716-446655440001"), // Тот же пользователь 1
        fileName = "java_developer_resume.pdf",
        fileKey = "uploads/660e8400-e29b-41d4-a716-446655440002/java_developer_resume.pdf",
        fileSize = 876543,
        uploadedAt = Instant.parse("2023-12-01T11:30:00Z"),
        status = ResumeProcessingStatus.PROCESSING,
        analysis = null
    )

    private val RESUME_PYTHON_DEV = Resume(
        id = ResumeId("660e8400-e29b-41d4-a716-446655440003"),
        vacancyId = VacancyId("550e8400-e29b-41d4-a716-446655440001"),
        userId = UserId("770e8400-e29b-41d4-a716-446655440002"), // Пользователь 2
        fileName = "python_developer_resume.pdf",
        fileKey = "uploads/660e8400-e29b-41d4-a716-446655440003/python_developer_resume.pdf",
        fileSize = 756432,
        uploadedAt = Instant.parse("2023-12-01T12:30:00Z"),
        status = ResumeProcessingStatus.ACCEPTED,
        analysis = ResumeAnalysisStub.PYTHON_ANALYSIS
    )

    fun getFileContent(fileKey: String): ByteArray {
        val pdfStubBase64 = "JVBERi0xLjQKJeLjz9MKMSAwIG9iago8PAovVHlwZSAvQ2F0YWxvZwovUGFnZXMgMiAwIFIKPj4KZW5kb2JqCjIgMCBvYmoKPDwKL1R5cGUgL1BhZ2VzCi9LaWRzIFsgMyAwIFIgXQovQ291bnQgMQo+PgplbmRvYmoKMyAwIG9iago8PAovVHlwZSAvUGFnZQovUGFyZW50IDIgMCBSCi9SZXNvdXJjZXMgPDwKL0ZvbnQgPDwKL0YxIDQgMCBSCj4+Cj4+Ci9NZWRpYUJveCBbIDAgMCA2MTIgNzkyIF0KL0NvbnRlbnRzIDUgMCBSCj4+CmVuZG9iago0IDAgb2JqCjw8Ci9UeXBlIC9Gb250Ci9TdWJ0eXBlIC9UeXBlMQovQmFzZUZvbnQgL0hlbHZldGljYQo+PgplbmRvYmoKNSAwIG9iago8PAovTGVuZ3RoIDQ0Cj4+CnN0cmVhbQpCVApBVDUCqICqICdoJ1RqKENWJykqJAgpCkVUCmVuZHN0cmVhbQplbmRvYmoKeHJlZgowIDYKMDAwMDAwMDAwMCA2NTUzNSBmCjAwMDAwMDAwMDkgMDAwMDAgbgowMDAwMDAwMDc0IDAwMDAwIG4KMDAwMDAwMDEyMCAwMDAwMCBuCjAwMDAwMDAyNjUgMDAwMDAgbgowMDAwMDAwMzI0IDAwMDAwIG4KdHJhaWxlcgo8PAovU2l6ZSA2Ci9Sb290IDEgMCBSCj4+CnN0YXJ0eHJlZgo0MTcKJSVFT0YK"
        
        return Base64.getDecoder().decode(pdfStubBase64)
    }

}