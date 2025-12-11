package ai.snapmatch.ktor.ws.v1.handlers

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.transport.ktor.ws.ai.snapmatch.ktor.ws.v1.repo.KtorWsSession
import com.auth0.jwt.JWT
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("wsHandler")

suspend fun WebSocketServerSession.wsHandler(appSettings: IAppSettings) {
    val request = call.request
    val token = request.queryParameters["token"]

    if (token.isNullOrBlank()) {
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Token required"))
        return
    }

    // Ручная декодировка токена
    val userId = try {
        JWT.decode(token).subject ?: throw IllegalArgumentException("Missing subject")
    } catch (e: Exception) {
        logger.error("Invalid token: ${e.message}")
        close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
        return
    }
    val wsSession = KtorWsSession(this, userId)
    val sessionRepo = appSettings.corSettings.wsSessionRepo

    try {
        sessionRepo.add(wsSession)
        logger.info("WebSocket connected: userId=$userId")

        send(Frame.Text("""{"type":"CONNECTED","userId":"$userId"}"""))

        for (frame in incoming) {
            when (frame) {
                is Frame.Text -> logger.debug("Received: ${frame.readText()}")
                is Frame.Ping -> send(Frame.Pong(frame.data))
                else -> {}
            }
        }
    } finally {
        sessionRepo.remove(wsSession)
        logger.info("WebSocket disconnected: userId=$userId")
    }
}