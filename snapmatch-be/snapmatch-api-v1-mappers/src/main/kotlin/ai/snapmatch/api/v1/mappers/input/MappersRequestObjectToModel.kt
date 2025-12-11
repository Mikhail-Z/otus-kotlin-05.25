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

fun ResumeUploadObjectDto.toModel(): Resume = Resume(
    id = this.id.toResumeId(),
    vacancyId = this.vacancyId.toVacancyId(),
    fileName = this.file.fileName
)

fun ResumeGetObjectDto.toModel(): Resume = Resume(
    id = this.id.toResumeId(),
)

fun ResumeDownloadObjectDto.toModel(): Resume = Resume(
    fileKey = this.fileKey
)

fun PaginationRequestObjectDto.toModel() = PaginationInput(
    page = this.page,
    perPage = this.perPage,
)

fun VacancyGetObjectDto.toModel() = Vacancy(
    id = this.id.toVacancyId()
)

fun VacancyCreateObjectDto.toModel() = Vacancy(
    title = this.title,
    description = this.description,
    companyName = this.companyName,
    location = this.location ?: "",
    scoreThreshold = this.scoreThreshold,
    minExperienceYears = this.minExperienceYears,
    skills = this.skills,
    salaryFrom = this.salaryFrom ?: Int.NONE,
    salaryTo = this.salaryTo ?: Int.NONE,
)

fun VacancyDeleteObjectDto.toModel() = Vacancy(
    id = this.id.toVacancyId()
)

fun VacancyFilterObjectDto?.toModel() = VacancyFilter(
    searchString = this?.searchString ?: "",
    skills = this?.skills ?: emptyList(),
    minSalary = this?.minSalary ?: Int.NONE,
    maxSalary = this?.maxSalary ?: Int.NONE,
    minExperienceYears = this?.minExperienceYears ?: Int.NONE,
    location = this?.location ?: ""
)