package ai.snapmatch.app.common.api

import ai.snapmatch.common.models.Resume

interface IResumeService {
    suspend fun processResume(resume: Resume)

    companion object {
        val NONE = object : IResumeService {
            override suspend fun processResume(resume: Resume) {
                // no-op
            }
        }
    }
}