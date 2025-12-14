package ai.snapmatch.api.v1.mappers.input

import ai.snapmatch.api.v1.models.PaginationRequestObjectDto
import ai.snapmatch.api.v1.models.ResumeDownloadObjectDto
import ai.snapmatch.api.v1.models.ResumeGetObjectDto
import ai.snapmatch.api.v1.models.ResumeUploadObjectDto
import ai.snapmatch.api.v1.models.VacancyCreateObjectDto
import ai.snapmatch.api.v1.models.VacancyDeleteObjectDto
import ai.snapmatch.api.v1.models.VacancyFilterObjectDto
import ai.snapmatch.api.v1.models.VacancyGetObjectDto
import ai.snapmatch.common.NONE
import ai.snapmatch.common.models.PaginationInput
import ai.snapmatch.common.models.Resume
import ai.snapmatch.common.models.Vacancy
import ai.snapmatch.common.models.VacancyFilter
import ai.snapmatch.api.v1.mappers.extensions.toResumeId
import ai.snapmatch.api.v1.mappers.extensions.toVacancyId

fun ResumeUploadObjectDto?.toModel(): Resume = this?.let {
    Resume(
        id = it.id.toResumeId(),
        vacancyId = it.vacancyId.toVacancyId(),
        fileName = it.file.fileName
    )
} ?: Resume()

fun ResumeGetObjectDto?.toModel(): Resume = this?.let {
    Resume(
        id = it.id.toResumeId(),
    )
} ?: Resume()

fun ResumeDownloadObjectDto?.toModel(): Resume = this?.let {
    Resume(
        fileKey = it.fileKey
    )
} ?: Resume()

fun PaginationRequestObjectDto?.toModel() = this?.let {
    PaginationInput(
        page = it.page,
        perPage = it.perPage,
    )
} ?: PaginationInput(page = 0, perPage = 10)

fun VacancyGetObjectDto?.toModel() = this?.let {
    Vacancy(
        id = it.id.toVacancyId()
    )
} ?: Vacancy()

fun VacancyCreateObjectDto?.toModel() = this?.let {
    Vacancy(
        title = it.title,
        description = it.description,
        companyName = it.companyName,
        location = it.location ?: "",
        scoreThreshold = it.scoreThreshold,
        minExperienceYears = it.minExperienceYears,
        skills = it.skills,
        salaryFrom = it.salaryFrom ?: Int.NONE,
        salaryTo = it.salaryTo ?: Int.NONE,
    )
} ?: Vacancy()

fun VacancyDeleteObjectDto?.toModel() = this?.let {
    Vacancy(
        id = it.id.toVacancyId()
    )
} ?: Vacancy()

fun VacancyFilterObjectDto?.toModel() = VacancyFilter(
    searchString = this?.searchString ?: "",
    skills = this?.skills ?: emptyList(),
    minSalary = this?.minSalary ?: Int.NONE,
    maxSalary = this?.maxSalary ?: Int.NONE,
    minExperienceYears = this?.minExperienceYears ?: Int.NONE,
    location = this?.location ?: ""
)