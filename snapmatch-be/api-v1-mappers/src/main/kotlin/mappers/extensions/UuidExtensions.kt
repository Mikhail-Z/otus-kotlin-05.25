package mappers.extensions

import ai.snapmatch.common.models.ResumeAnalysisId
import ai.snapmatch.common.models.ResumeId
import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.VacancyId

fun java.util.UUID?.toResumeId() = this?.toString()?.toResumeId() ?: ResumeId.NONE
fun java.util.UUID?.toVacancyId() = this?.toString()?.toVacancyId() ?: VacancyId.NONE
fun java.util.UUID?.toUserId() = this?.toString()?.toUserId() ?: UserId.NONE
fun java.util.UUID?.toResumeAnalysisId() = this?.toString()?.toResumeAnalysisId() ?: ResumeAnalysisId.NONE
