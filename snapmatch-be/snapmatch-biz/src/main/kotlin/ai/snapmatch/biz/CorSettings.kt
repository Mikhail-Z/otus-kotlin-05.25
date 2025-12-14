package ai.snapmatch.biz

import ai.snapmatch.app.common.api.ICorSettings
import ai.snapmatch.app.common.api.IWsSessionRepo
import ai.snapmatch.app.common.api.IResumeService

data class CorSettings(
    override val wsSessionRepo: IWsSessionRepo,
    override val resumeService: IResumeService
): ICorSettings {
    companion object {
        val NONE = CorSettings(
            wsSessionRepo = IWsSessionRepo.NONE,
            resumeService = IResumeService.NONE,
        )
    }
}