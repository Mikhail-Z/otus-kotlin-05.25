package mappers.extensions

import ai.snapmatch.common.models.ResumeAnalysisId
import ai.snapmatch.common.models.ResumeId
import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.VacancyId

fun String?.toResumeId() = this?.let { ResumeId(it) } ?: ResumeId.NONE
fun String?.toVacancyId() = this?.let { VacancyId(it) } ?: VacancyId.NONE
fun String?.toUserId() = this?.let { UserId(it) } ?: UserId.NONE
fun String?.toResumeAnalysisId() = this?.let { ResumeAnalysisId(it) } ?: ResumeAnalysisId.NONE