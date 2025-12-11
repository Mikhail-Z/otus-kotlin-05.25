package ai.snapmatch.api.v1.mappers.output

import ai.snapmatch.api.v1.models.*
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.State

// Resume responses
fun Context.toResumeUploadResponse() = ResumeUploadResponseDto(
    responseType = "resumeUpload",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    resume = this.resumeResponse.toResponseObject()
)

fun Context.toResumeGetResponse() = ResumeGetResponseDto(
    responseType = "resumeGet",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    resume = this.resumeResponse.toResponseObject()
)

fun Context.toMyResumesResponse() = ResumesPaginationResponseDto(
    responseType = "myResumes",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    resumes = this.resumesResponse.toResumeResponseObjects(),
    pagination = this.paginationOutput.toDto()
)

// Vacancy responses
fun Context.toVacancyCreateResponse() = VacancyCreateResponseDto(
    responseType = "vacancyCreate",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    vacancy = this.vacancyResponse.toResponseObject()
)

fun Context.toVacancyGetResponse() = VacancyGetResponseDto(
    responseType = "vacancyGet",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    vacancy = this.vacancyResponse.toResponseObject()
)

fun Context.toVacancySearchResponse() = VacanciesPaginationResponseDto(
    responseType = "vacancySearch",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    vacancies = this.vacanciesResponse.toVacancyResponseObjects(),
    pagination = PaginationResponseObjectDto(
        page = this.paginationOutput.page,
        perPage = this.paginationOutput.perPage,
        totalElements = this.paginationOutput.totalElements,
        hasNext = this.paginationOutput.hasNext
    )
)

fun Context.toVacancyResumesResponse() = ResumesPaginationResponseDto(
    responseType = "vacancyResumes",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    resumes = this.resumesResponse.toResumeResponseObjects(),
    pagination = PaginationResponseObjectDto(
        page = this.paginationOutput.page,
        perPage = this.paginationOutput.perPage,
        totalElements = this.paginationOutput.totalElements,
        hasNext = this.paginationOutput.hasNext
    )
)

fun Context.toVacancyDeleteResponse() = VacancyDeleteResponseDto(
    responseType = "vacancyDelete",
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    vacancy = this.vacancyResponse.toResponseObject()
)

// Legacy function - можно удалить
fun Context.toResponseDto() = ResumeGetResponseDto(
    result = this.state.toResultDto(),
    errors = this.errors.toDto(),
    resume = this.resumeResponse.toResponseObject()
)

private fun State.toResultDto() = when (this) {
    State.RUNNING -> ResponseResultDto.SUCCESS
    State.FINISHING -> ResponseResultDto.SUCCESS
    State.FAILING -> ResponseResultDto.ERROR
    State.NONE -> null
}

// Временно закомментировано - нужно найти правильное имя DTO класса для пагинации
// private fun PaginationOutput.toDto() = ...()