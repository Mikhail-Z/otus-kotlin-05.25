package ai.snapmatch.stubs

import ai.snapmatch.common.models.LLMProvider
import ai.snapmatch.common.models.ResumeAnalysis
import ai.snapmatch.common.models.ResumeAnalysisDetails
import ai.snapmatch.common.models.ResumeAnalysisId
import ai.snapmatch.common.models.ResumeId
import kotlinx.datetime.Instant

object ResumeAnalysisStub {
    fun get(): ResumeAnalysis = KOTLIN_ANALYSIS.copy()

    fun prepareResult(block: ResumeAnalysis.() -> Unit): ResumeAnalysis = get().apply(block)

    val KOTLIN_ANALYSIS = ResumeAnalysis(
        id = ResumeAnalysisId("8b83ec7f-ceff-4596-a841-90484546f652"),
        resumeId = ResumeId("660e8400-e29b-41d4-a716-446655440001"),
        score = 85,
        details = ResumeAnalysisDetails(
            strengths = listOf(
                "Сильные навыки программирования на Kotlin",
                "Опыт разработки микросервисов",
                "Знание современных технологий"
            ),
            weaknesses = listOf(
                "Недостаточный опыт с облачными платформами",
                "Мало опыта с контейнеризацией"
            ),
            hrRecommendations = listOf(
                "Отличное знание Kotlin и Spring Boot",
                "Хороший опыт работы с базами данных",
                "Рекомендуется изучить Kubernetes"
            ),
            summary = "Кандидат с сильными навыками Kotlin разработки, подходит для позиции Senior Developer. Рекомендуется дополнительное изучение cloud-native технологий."
        ),
        llmProvider = LLMProvider.GIGACHAT,
        llmModel = "GigaChat-Pro",
        createdAt = Instant.parse("2023-12-01T10:45:00Z")
    )

    val PYTHON_ANALYSIS = ResumeAnalysis(
        id = ResumeAnalysisId("172bc61c-6333-4a31-80e9-244f1d5b6c2b"),
        resumeId = ResumeId("660e8400-e29b-41d4-a716-446655440003"),
        score = 68,
        details = ResumeAnalysisDetails(
            strengths = listOf(
                "Strong Python programming skills",
                "Web development experience",
                "Database knowledge"
            ),
            weaknesses = listOf(
                "Limited experience with async programming",
                "No experience with modern Python frameworks"
            ),
            hrRecommendations = listOf(
                "Хорошее знание Python и Django",
                "Стоит изучить FastAPI",
                "Рекомендуется освоить async/await"
            ),
            summary = "Компетентный Python разработчик с опытом web-разработки. Стоит развивать знания современных фреймворков."
        ),
        llmProvider = LLMProvider.GIGACHAT,
        llmModel = "GigaChat-Standard",
        createdAt = Instant.parse("2023-12-01T12:45:00Z")
    )
}