package ai.snapmatch.common.models

import ai.snapmatch.common.NONE

data class VacancyFilter(
    val searchString: String = "",
    val skills: List<String> = emptyList(),
    val minSalary: Int = Int.NONE,
    val maxSalary: Int = Int.NONE,
    val minExperienceYears: Int = Int.NONE,
    val location: String = ""
)