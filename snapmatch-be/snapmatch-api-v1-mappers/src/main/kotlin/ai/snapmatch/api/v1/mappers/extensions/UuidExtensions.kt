package ai.snapmatch.api.v1.mappers.extensions

import ai.snapmatch.common.models.ResumeAnalysisId
import ai.snapmatch.common.models.ResumeId
import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.VacancyId
import java.util.UUID

fun UUID?.toResumeId() = this?.toString()?.toResumeId() ?: ResumeId.NONE
fun UUID?.toVacancyId() = this?.toString()?.toVacancyId() ?: VacancyId.NONE
fun UUID?.toUserId() = this?.toString()?.toUserId() ?: UserId.NONE
fun UUID?.toResumeAnalysisId() = this?.toString()?.toResumeAnalysisId() ?: ResumeAnalysisId.NONE
