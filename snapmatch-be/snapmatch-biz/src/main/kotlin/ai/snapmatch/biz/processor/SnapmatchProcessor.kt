package ai.snapmatch.biz.processor

import ai.snapmatch.app.common.api.ISnapmatchProcessor
import ai.snapmatch.common.Context
import ai.snapmatch.biz.CorSettings
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.SnapmatchError
import ai.snapmatch.common.models.State
import ai.snapmatch.stubs.ResumeStub
import ai.snapmatch.stubs.VacancyStub

@Suppress("unused", "RedundantSuspendModifier")
class SnapmatchProcessor(val corSettings: CorSettings): ISnapmatchProcessor {

    override suspend fun exec(ctx: Context) {
        when (ctx.command) {
            Command.UPLOAD_RESUME -> {
                val resume = ResumeStub.get()
                corSettings.resumeService.processResume(resume)
                ctx.resumeResponse = resume
                ctx.state = State.FINISHING
            }
            Command.GET_RESUME -> {
                ctx.resumeResponse = ResumeStub.get()
                ctx.state = State.FINISHING
            }
            Command.DOWNLOAD_RESUME -> {
                ctx.resumeResponse = ResumeStub.get()
                ctx.state = State.FINISHING
            }
            Command.GET_MY_RESUMES -> {
                ctx.resumesResponse.addAll(ResumeStub.prepareMyResumesList())
                ctx.state = State.FINISHING
            }
            Command.CREATE_VACANCY -> {
                ctx.vacancyResponse = VacancyStub.get()
                ctx.state = State.FINISHING
            }
            Command.GET_VACANCY -> {
                ctx.vacancyResponse = VacancyStub.get()
                ctx.state = State.FINISHING
            }
            Command.DELETE_VACANCY -> {
                ctx.vacancyResponse = VacancyStub.prepareResult { 
                    isActive = false 
                }
                ctx.state = State.FINISHING
            }
            Command.SEARCH_VACANCIES -> {
                ctx.vacanciesResponse.addAll(VacancyStub.prepareSearchList())
                ctx.state = State.FINISHING
            }
            Command.GET_VACANCY_RESUMES -> {
                ctx.resumesResponse.addAll(ResumeStub.prepareResumesList())
                ctx.state = State.FINISHING
            }
            Command.NONE -> {
                ctx.state = State.FAILING
                ctx.errors.add(
                    SnapmatchError(
                        code = "unknown-command",
                        group = "validation",
                        field = "command",
                        message = "Unknown command"
                    )
                )
            }
        }
    }
}