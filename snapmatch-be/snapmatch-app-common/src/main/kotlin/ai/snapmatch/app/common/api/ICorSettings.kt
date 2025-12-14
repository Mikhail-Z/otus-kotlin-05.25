package ai.snapmatch.app.common.api


interface ICorSettings {
    val wsSessionRepo: IWsSessionRepo
    val resumeService: IResumeService
}