package ai.snapmatch.common.models

import ai.snapmatch.common.NONE
import kotlinx.datetime.Instant

data class ResumeAnalysis(
    val id: ResumeAnalysisId = ResumeAnalysisId.NONE,
    val resumeId: ResumeId = ResumeId.NONE,
    val score: Int = Int.NONE,
    val details: ResumeAnalysisDetails = ResumeAnalysisDetails(),
    val llmProvider: LLMProvider = LLMProvider.GIGACHAT,
    val llmModel: String = "",
    val createdAt: Instant = Instant.NONE
) {
    fun isEmpty() = this == NONE

    companion object {
        private val NONE = ResumeAnalysis()
    }
}

data class ResumeAnalysisDetails(
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val hrRecommendations: List<String> = emptyList(),
    val summary: String = ""
) {
    fun isEmpty() = this == NONE

    companion object {
        private val NONE = ResumeAnalysisDetails()
    }
}