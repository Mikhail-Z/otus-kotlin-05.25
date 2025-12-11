package ai.snapmatch.common.models

import ai.snapmatch.common.NONE
import kotlinx.datetime.Instant

data class Resume(
    val id: ResumeId = ResumeId.NONE,
    val vacancyId: VacancyId = VacancyId.NONE,
    val userId: UserId = UserId.NONE,
    val fileName: String = "",
    val fileKey: String = "",
    val fileSize: Int = Int.Companion.NONE,
    val uploadedAt: Instant = Instant.NONE,
    val status: ResumeProcessingStatus = ResumeProcessingStatus.UPLOADED,
    val analysis: ResumeAnalysis? = null
) {
    fun isEmpty() = this == NONE

    companion object {
        private val NONE = Resume()
    }
}