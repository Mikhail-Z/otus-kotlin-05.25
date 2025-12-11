package ai.snapmatch.api.v1.mappers.input

import ai.snapmatch.api.v1.models.*
import ai.snapmatch.common.Context
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.WorkMode
import ai.snapmatch.common.stubs.DebugStubs

// Общая функция для обработки любого типа запроса
fun Context.fromRequest(request: IRequestDto) {
    when (request) {
        is ResumeUploadRequestDto -> fromRequest(request)
        is ResumeGetRequestDto -> fromRequest(request)
        is ResumeDownloadRequestDto -> fromRequest(request)
        is MyResumesGetPaginationRequestDto -> fromRequest(request)
        is VacancyResumesGetPaginationRequestDto -> fromRequest(request)
        is VacancyCreateRequestDto -> fromRequest(request)
        is VacancyGetRequestDto -> fromRequest(request)
        is VacancyDeleteRequestDto -> fromRequest(request)
        is VacancySearchPaginationRequestDto -> fromRequest(request)
        else -> throw IllegalArgumentException("Unknown request type: ${request::class}")
    }
}

fun Context.fromRequest(request: ResumeUploadRequestDto) {
    command = Command.UPLOAD_RESUME
    resumeRequest = request.resume.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: ResumeGetRequestDto) {
    command = Command.GET_RESUME
    resumeRequest = request.resume.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: ResumeDownloadRequestDto) {
    command = Command.DOWNLOAD_RESUME
    resumeRequest = request.resume.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()}

fun Context.fromRequest(request: MyResumesGetPaginationRequestDto) {
    command = Command.GET_MY_RESUMES
    paginationInput = request.pagination.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: VacancyResumesGetPaginationRequestDto) {
    command = Command.GET_VACANCY_RESUMES
    vacancyRequest = request.vacancy.toModel()
    paginationInput = request.pagination.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: VacancyCreateRequestDto) {
    command = Command.CREATE_VACANCY
    vacancyRequest = request.vacancy.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: VacancyGetRequestDto) {
    command = Command.GET_VACANCY
    vacancyRequest = request.vacancy.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: VacancyDeleteRequestDto) {
    command = Command.DELETE_VACANCY
    vacancyRequest = request.vacancy.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

fun Context.fromRequest(request: VacancySearchPaginationRequestDto) {
    command = Command.SEARCH_VACANCIES
    vacancyFilter = request.vacancyFilter.toModel()
    paginationInput = request.pagination.toModel()
    workMode = request.debug.dtoToWorkMode()
    stubCase = request.debug.dtoToStubCase()
}

private fun DebugDto?.dtoToWorkMode(): WorkMode = when (this?.mode) {
    DebugModeDto.PROD -> WorkMode.PROD
    DebugModeDto.TEST -> WorkMode.TEST
    DebugModeDto.STUB -> WorkMode.STUB
    null -> WorkMode.PROD
}

private fun DebugDto.dtoToStubCase(): DebugStubs = when (this.stub) {
    DebugStubsDto.SUCCESS -> DebugStubs.SUCCESS
    DebugStubsDto.NOT_FOUND -> DebugStubs.NOT_FOUND
    DebugStubsDto.BAD_ID -> DebugStubs.BAD_ID
    DebugStubsDto.VALIDATION_ERROR -> DebugStubs.VALIDATION_ERROR
    DebugStubsDto.BAD_FILE -> DebugStubs.BAD_FILE
    DebugStubsDto.NO_RIGHTS -> DebugStubs.NO_RIGHTS
    null -> DebugStubs.NONE
}