package ai.snapmatch.api.v1.mappers.output

import ai.snapmatch.api.v1.models.*
import ai.snapmatch.common.models.LLMProvider
import ai.snapmatch.common.models.PaginationOutput
import ai.snapmatch.common.models.Resume
import ai.snapmatch.common.models.ResumeAnalysis
import ai.snapmatch.common.models.ResumeAnalysisDetails
import ai.snapmatch.common.models.ResumeAnalysisId
import ai.snapmatch.common.models.ResumeId
import ai.snapmatch.common.models.ResumeProcessingStatus
import ai.snapmatch.common.models.SnapmatchError
import ai.snapmatch.common.models.UserId
import ai.snapmatch.common.models.Vacancy
import ai.snapmatch.common.models.VacancyId
import java.util.UUID

// Resume mappings
fun Resume.toResponseObject() = ResumeResponseObjectDto(
    id = this.id.toUUID(),
    vacancyId = this.vacancyId.toUUID(),
    userId = this.userId.toUUID(),
    fileName = this.fileName,
    fileKey = this.fileKey,
    fileSize = this.fileSize,
    uploadedAt = this.uploadedAt,
    status = this.status.toDto(),
    analysis = this.analysis?.toDto()
)

fun List<Resume>.toResumeResponseObjects(): List<ResumeResponseObjectDto>? = this
    .map { it.toResponseObject() }
    .toList()
    .takeIf { it.isNotEmpty() }

// Vacancy mappings
fun Vacancy.toResponseObject() = VacancyResponseObjectDto(
    id = this.id.toUUID(),
    title = this.title,
    description = this.description,
    scoreThreshold = this.scoreThreshold,
    location = this.location.takeIf { it.isNotEmpty() },
    minExperienceYears = this.minExperienceYears,
    skills = this.skills,
    salaryFrom = this.salaryFrom,
    salaryTo = this.salaryTo,
    isActive = this.isActive,
    createdAt = this.createdAt,
    createdBy = this.createdBy.toUUID(),
    companyName = this.companyName
)

fun List<Vacancy>.toVacancyResponseObjects(): List<VacancyResponseObjectDto>? = this
    .map { it.toResponseObject() }
    .toList()
    .takeIf { it.isNotEmpty() }

// ResumeAnalysis mappings
fun ResumeAnalysis.toDto() = ResumeAnalysisResponseObjectDto(
    id = this.id.toUUID(),
    resumeId = this.resumeId.toUUID(),
    score = this.score,
    details = this.details.toDto(),
    llmProvider = this.llmProvider.toDto(),
    llmModel = this.llmModel,
    createdAt = this.createdAt
)

fun ResumeAnalysisDetails.toDto() = ResumeAnalysisDetailsDto(
    strengths = this.strengths,
    weaknesses = this.weaknesses,
    hrRecommendations = this.hrRecommendations,
    summary = this.summary
)

fun PaginationOutput.toDto() = PaginationResponseObjectDto(
    page = this.page,
    perPage = this.perPage,
    totalElements = this.totalElements,
    hasNext = this.hasNext
)

// Enums mappings
fun ResumeProcessingStatus.toDto() = when (this) {
    ResumeProcessingStatus.UPLOADED -> ResumeProcessingStatusDto.UPLOADED
    ResumeProcessingStatus.PROCESSING -> ResumeProcessingStatusDto.PROCESSING
    ResumeProcessingStatus.ACCEPTED -> ResumeProcessingStatusDto.ACCEPTED
    ResumeProcessingStatus.REJECTED -> ResumeProcessingStatusDto.REJECTED
}

fun LLMProvider.toDto() = when (this) {
    LLMProvider.GIGACHAT -> LLMProviderDto.GIGACHAT
}



// Error mappings
fun SnapmatchError.toDto() = ErrorDto(
    code = this.code.takeIf { it.isNotBlank() },
    group = this.group.takeIf { it.isNotBlank() },
    field = this.field.takeIf { it.isNotBlank() },
    message = this.message.takeIf { it.isNotBlank() }
)

fun List<SnapmatchError>.toDto(): List<ErrorDto>? = this
    .map { it.toDto() }
    .takeIf { it.isNotEmpty() }

// Pagination mappings - будет реализован позже когда найдем правильное имя DTO класса
// fun PaginationOutput.toDto() = ...()

// ID conversions
internal fun ResumeId.toDto() = takeIf { it != ResumeId.NONE }?.asString()
internal fun VacancyId.toDto() = takeIf { it != VacancyId.NONE }?.asString()
internal fun UserId.toDto() = takeIf { it != UserId.NONE }?.asString()
internal fun ResumeAnalysisId.toDto() = takeIf { it != ResumeAnalysisId.NONE }?.asString()

// UUID conversions
internal fun ResumeId.toUUID(): UUID = this.asUUID() ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
internal fun VacancyId.toUUID(): UUID = this.asUUID() ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
internal fun UserId.toUUID(): UUID = this.asUUID() ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
internal fun ResumeAnalysisId.toUUID(): UUID = this.asUUID() ?: UUID.fromString("00000000-0000-0000-0000-000000000000")
