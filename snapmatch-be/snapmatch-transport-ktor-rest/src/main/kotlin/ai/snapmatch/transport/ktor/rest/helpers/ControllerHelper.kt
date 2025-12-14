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
        lateinit var ctx: Context
        val response = appSettings.processRequest(
            {
                fromRequest(request)
                ctx = this  // Capture context
            },
            toResponse,
            clazz
        )
        respond(HttpStatusCode.OK, response)

        // Send WebSocket notification for resume upload
        if (ctx.command == ai.snapmatch.common.models.Command.UPLOAD_RESUME &&
            ctx.resumeResponse.userId.asString().isNotEmpty()) {
            try {
                application.log.info("Attempting to send WebSocket notification: response type = ${response::class.qualifiedName}, is IResponseDto = ${response is IResponseDto}")
                appSettings.corSettings.wsSessionRepo.sendToUser(
                    ctx.resumeResponse.userId.asString(),
                    response
                )
                application.log.info("WebSocket notification sent for resume upload to user: ${ctx.resumeResponse.userId.asString()}")
            } catch (e: Exception) {
                application.log.error("Failed to send WebSocket notification: ${e.message}", e)
            }
        }
    } catch (e: Exception) {
        application.log.error("Error processing $logId", e)
        respond(HttpStatusCode.InternalServerError, "Internal server error")
    }
}