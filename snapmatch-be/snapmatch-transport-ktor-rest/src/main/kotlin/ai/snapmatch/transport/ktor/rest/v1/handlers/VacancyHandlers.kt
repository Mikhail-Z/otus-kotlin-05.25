package ai.snapmatch.transport.ktor.rest.v1.handlers

import ai.snapmatch.api.v1.models.ResumesPaginationResponseDto
import ai.snapmatch.api.v1.models.VacanciesPaginationResponseDto
import ai.snapmatch.api.v1.models.VacancyCreateRequestDto
import ai.snapmatch.api.v1.models.VacancyCreateResponseDto
import ai.snapmatch.api.v1.models.VacancyDeleteRequestDto
import ai.snapmatch.api.v1.models.VacancyDeleteResponseDto
import ai.snapmatch.api.v1.models.VacancyGetRequestDto
import ai.snapmatch.api.v1.models.VacancyGetResponseDto
import ai.snapmatch.api.v1.models.VacancyResumesGetPaginationRequestDto
import ai.snapmatch.api.v1.models.VacancySearchPaginationRequestDto
import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.transport.ktor.rest.helpers.processV1
import io.ktor.server.application.ApplicationCall
import ai.snapmatch.api.v1.mappers.output.toVacancyCreateResponse
import ai.snapmatch.api.v1.mappers.output.toVacancyDeleteResponse
import ai.snapmatch.api.v1.mappers.output.toVacancyGetResponse
import ai.snapmatch.api.v1.mappers.output.toVacancyResumesResponse
import ai.snapmatch.api.v1.mappers.output.toVacancySearchResponse
import kotlin.reflect.KClass

val clVacancyCreate: KClass<*> = ApplicationCall::vacancyCreate::class
suspend fun ApplicationCall.vacancyCreate(appSettings: IAppSettings) =
    processV1<VacancyCreateRequestDto, VacancyCreateResponseDto>(appSettings, clVacancyCreate, "vacancy-create") {
        toVacancyCreateResponse()
    }

val clVacancyGet: KClass<*> = ApplicationCall::vacancyGet::class
suspend fun ApplicationCall.vacancyGet(appSettings: IAppSettings) =
    processV1<VacancyGetRequestDto, VacancyGetResponseDto>(appSettings, clVacancyGet, "vacancy-get") {
        toVacancyGetResponse()
    }

val clVacancySearch: KClass<*> = ApplicationCall::vacancySearch::class
suspend fun ApplicationCall.vacancySearch(appSettings: IAppSettings) =
    processV1<VacancySearchPaginationRequestDto, VacanciesPaginationResponseDto>(appSettings, clVacancySearch, "vacancy-search") {
        toVacancySearchResponse()
    }

val clVacancyResumes: KClass<*> = ApplicationCall::vacancyResumes::class
suspend fun ApplicationCall.vacancyResumes(appSettings: IAppSettings) =
    processV1<VacancyResumesGetPaginationRequestDto, ResumesPaginationResponseDto>(appSettings, clVacancyResumes, "vacancy-resumes") {
        toVacancyResumesResponse()
    }

val clVacancyDelete: KClass<*> = ApplicationCall::vacancyDelete::class
suspend fun ApplicationCall.vacancyDelete(appSettings: IAppSettings) =
    processV1<VacancyDeleteRequestDto, VacancyDeleteResponseDto>(appSettings, clVacancyDelete, "vacancy-delete") {
        toVacancyDeleteResponse()
    }