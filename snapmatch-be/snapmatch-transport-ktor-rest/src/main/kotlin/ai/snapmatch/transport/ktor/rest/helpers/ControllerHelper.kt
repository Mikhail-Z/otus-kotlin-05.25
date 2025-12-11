package ai.snapmatch.transport.ktor.rest.helpers

import ai.snapmatch.api.v1.models.IRequestDto
import ai.snapmatch.api.v1.models.IResponseDto
import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.processRequest
import ai.snapmatch.common.Context
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ai.snapmatch.api.v1.mappers.input.fromRequest
import kotlin.reflect.KClass

suspend inline fun <reified Q : IRequestDto, reified R : IResponseDto> ApplicationCall.processV1(
    appSettings: IAppSettings,
    clazz: KClass<*>,
    logId: String,
    crossinline toResponse: Context.() -> R
) {
    try {
        val request = receive<Q>()
        val response = appSettings.processRequest(
            { fromRequest(request) },
            toResponse,
            clazz
        )
        respond(HttpStatusCode.OK, response)
    } catch (e: Exception) {
        application.log.error("Error processing $logId", e)
        respond(HttpStatusCode.InternalServerError, "Internal server error")
    }
}