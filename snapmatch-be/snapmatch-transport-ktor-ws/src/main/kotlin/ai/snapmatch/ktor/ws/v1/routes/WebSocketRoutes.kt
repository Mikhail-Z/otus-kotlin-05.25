package ai.snapmatch.ktor.ws.v1.routes

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.ktor.ws.v1.handlers.wsHandler
import io.ktor.server.routing.*
import io.ktor.server.websocket.*


fun Route.wsRoute(appSettings: IAppSettings) {
    // Применяем JWT authentication
    //authenticate("auth-jwt") {
        webSocket("/ws") {
            wsHandler(appSettings)
        }
    //}
}