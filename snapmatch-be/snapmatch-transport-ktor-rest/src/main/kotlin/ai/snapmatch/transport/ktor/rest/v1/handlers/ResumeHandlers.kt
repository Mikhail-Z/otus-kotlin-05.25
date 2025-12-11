package ai.snapmatch.transport.ktor.rest.v1.handlers

import ai.snapmatch.api.v1.models.*
import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.transport.ktor.rest.helpers.processV1
import ai.snapmatch.stubs.ResumeStub
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ai.snapmatch.api.v1.mappers.output.toMyResumesResponse
import ai.snapmatch.api.v1.mappers.output.toResumeGetResponse
import ai.snapmatch.api.v1.mappers.output.toResumeUploadResponse
import kotlin.reflect.KClass

val clResumeUpload: KClass<*> = ApplicationCall::resumeUpload::class
suspend fun ApplicationCall.resumeUpload(appSettings: IAppSettings) =
    processV1<ResumeUploadRequestDto, ResumeUploadResponseDto>(appSettings, clResumeUpload, "resume-upload") {
        toResumeUploadResponse()
    }

val clResumeGet: KClass<*> = ApplicationCall::resumeGet::class
suspend fun ApplicationCall.resumeGet(appSettings: IAppSettings) =
    processV1<ResumeGetRequestDto, ResumeGetResponseDto>(appSettings, clResumeGet, "resume-get") {
        toResumeGetResponse()
    }

val clResumeMy: KClass<*> = ApplicationCall::resumeMy::class
suspend fun ApplicationCall.resumeMy(appSettings: IAppSettings) =
    processV1<MyResumesGetPaginationRequestDto, ResumesPaginationResponseDto>(appSettings, clResumeMy, "resume-my") {
        toMyResumesResponse()
    }

val clResumeDownload: KClass<*> = ApplicationCall::resumeDownload::class
suspend fun ApplicationCall.resumeDownload(appSettings: IAppSettings) {
    try {
        // Получаем резюме (пока из заглушки)
        val resume = ResumeStub.get()
        val fileName = resume.fileName
        
        // Получаем содержимое файла из заглушки
        val fileBytes = ResumeStub.getFileContent(resume.fileKey)

        // Отдаем файл как есть
        response.header(HttpHeaders.ContentDisposition, "attachment; filename=\"$fileName\"")
        respondBytes(fileBytes, ContentType.Application.Pdf)

    } catch (e: Exception) {
        respond(HttpStatusCode.InternalServerError, "Ошибка скачивания файла: ${e.message}")
    }
}