package ai.snapmatch.biz.service

import ai.snapmatch.app.common.api.IResumeService
import ai.snapmatch.app.common.api.IWsSessionRepo
import ai.snapmatch.common.models.Resume
import ai.snapmatch.stubs.ResumeAnalysisStub

class ResumeService(
    private val wsRepo: IWsSessionRepo,
): IResumeService {
    override suspend fun processResume(resume: Resume) {
        val analysis = ResumeAnalysisStub.KOTLIN_ANALYSIS
        // For now, just store the analysis in the resume
        // WebSocket notifications will be handled separately
        // TODO: Implement proper notification mechanism
    }
}