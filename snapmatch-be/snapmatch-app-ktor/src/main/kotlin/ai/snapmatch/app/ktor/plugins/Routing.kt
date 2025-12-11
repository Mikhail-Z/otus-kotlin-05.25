package ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.ktor.routes.v1.rest.v1Resume
import ai.snapmatch.app.ktor.routes.v1.rest.v1Vacancy
import ai.snapmatch.ktor.ws.v1.routes.wsRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(appSettings: IAppSettings) {
    routing {
        // REST транспорт
        v1Resume(appSettings)
        v1Vacancy(appSettings)

        // WebSocket транспорт
        wsRoute(appSettings)
    }
}