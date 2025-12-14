package ai.snapmatch.app.ktor

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.app.common.api.ICorSettings
import ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins.configureRouting
import ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins.configureSecurity
import ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins.configureSerialization
import ai.snapmatch.app.ktor.snapmatch.app.ktor.plugins.configureWebSockets
import ai.snapmatch.biz.CorSettings
import ai.snapmatch.biz.processor.SnapmatchProcessor
import ai.snapmatch.biz.service.ResumeService
import ai.snapmatch.transport.ktor.ws.ai.snapmatch.ktor.ws.v1.repo.KtorWsSessionRepo
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    val appSettings = initAppSettings()

    configureSecurity()
    configureWebSockets()
    configureSerialization()
    configureRouting(appSettings)
}

fun initAppSettings(): IAppSettings {
    val wsSessionRepo = KtorWsSessionRepo()
    val resumeService = ResumeService(wsSessionRepo)
    val corSettings = CorSettings(
        wsSessionRepo = wsSessionRepo,
        resumeService = resumeService,
    )
    val processor = SnapmatchProcessor(corSettings)

    return AppSettings(
        corSettings = corSettings,
        processor = processor
    )
}

data class AppSettings(
    override val processor: SnapmatchProcessor,
    override val corSettings: ICorSettings
) : IAppSettings