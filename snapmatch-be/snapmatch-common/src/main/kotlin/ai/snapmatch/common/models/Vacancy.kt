package ai.snapmatch.common.models

import ai.snapmatch.common.NONE
import kotlinx.datetime.Instant

data class Vacancy(
    val id: VacancyId = VacancyId.NONE,
    val title: String = "",
    val description: String = "",
    val scoreThreshold: Int = Int.Companion.NONE,
    val location: String = "",
    val minExperienceYears: Int = Int.NONE,
    val skills: List<String> = emptyList(),
    val salaryFrom: Int = Int.NONE,
    val salaryTo: Int = Int.NONE,
    var isActive: Boolean = false,
    val createdAt: Instant = Instant.NONE,
    val createdBy: UserId = UserId.NONE,
    val companyName: String = "",
) {
    fun isEmpty() = this == NONE

    companion object {
        private val NONE = Vacancy()
    }
}