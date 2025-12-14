package ai.snapmatch.app.ktor.routes.v1.rest

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyCreate
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyDelete
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyGet
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancyResumes
import ai.snapmatch.transport.ktor.rest.v1.handlers.vacancySearch
import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.v1Vacancy(appSettings: IAppSettings) {
    route("vacancy") {
        post("create") {
            call.vacancyCreate(appSettings)
        }
        post("get") {
            call.vacancyGet(appSettings)
        }
        post("search") {
            call.vacancySearch(appSettings)
        }
        post("resumes") {
            call.vacancyResumes(appSettings)
        }
        post("delete") {
            call.vacancyDelete(appSettings)
        }
    }
}