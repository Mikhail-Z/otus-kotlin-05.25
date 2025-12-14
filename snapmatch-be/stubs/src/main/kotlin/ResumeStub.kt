package ai.snapmatch.stubs

import ai.snapmatch.common.models.*
import kotlinx.datetime.Instant

object ResumeStub {
    fun get(): Resume = RESUME_KOTLIN_DEV.copy()

    fun prepareResult(block: Resume.() -> Unit): Resume = get().apply(block)

    fun prepareResumesList(vacancyId: String = "550e8400-e29b-41d4-a716-446655440001") = listOf(
        resumeKotlin("660e8400-e29b-41d4-a716-446655440011", vacancyId),
        resumeJava("660e8400-e29b-41d4-a716-446655440012", vacancyId),
        resumePython("660e8400-e29b-41d4-a716-446655440013", vacancyId),
    )

    fun prepareMyResumesList(userId: String = "770e8400-e29b-41d4-a716-446655440001") = listOf(
        resumeKotlin("660e8400-e29b-41d4-a716-446655440021", "550e8400-e29b-41d4-a716-446655440001").copy(userId = UserId(userId)),
        resumeJava("660e8400-e29b-41d4-a716-446655440022", "550e8400-e29b-41d4-a716-446655440002").copy(userId = UserId(userId)),
        resumePython("660e8400-e29b-41d4-a716-446655440023", "550e8400-e29b-41d4-a716-446655440001").copy(userId = UserId(userId)),
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

}