package ai.snapmatch.app.ktor.routes.v1.rest

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.transport.ktor.rest.v1.handlers.resumeDownload
import ai.snapmatch.transport.ktor.rest.v1.handlers.resumeGet
import ai.snapmatch.transport.ktor.rest.v1.handlers.resumeMy
import ai.snapmatch.transport.ktor.rest.v1.handlers.resumeUpload
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.v1Resume(appSettings: IAppSettings) {
    route("resume") {
        post("upload") {
            call.resumeUpload(appSettings)
        }
        post("get") {
            call.resumeGet(appSettings)
        }
        post("my") {
            call.resumeMy(appSettings)
        }
        post("download") {
            call.resumeDownload(appSettings)
        }
    }
}