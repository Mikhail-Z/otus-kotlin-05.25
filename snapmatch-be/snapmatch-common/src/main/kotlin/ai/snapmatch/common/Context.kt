package ai.snapmatch.common

import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.PaginationInput
import ai.snapmatch.common.models.PaginationOutput
import ai.snapmatch.common.models.RequestId
import ai.snapmatch.common.models.Resume
import ai.snapmatch.common.models.SnapmatchError
import ai.snapmatch.common.models.State
import ai.snapmatch.common.models.Vacancy
import ai.snapmatch.common.models.VacancyFilter
import ai.snapmatch.common.models.WorkMode
import ai.snapmatch.common.stubs.DebugStubs
import kotlinx.datetime.Instant

data class Context(
    var command: Command = Command.NONE,
    var state: State = State.NONE,
    val errors: MutableList<SnapmatchError> = mutableListOf(),

    var workMode: WorkMode = WorkMode.PROD,
    var stubCase: DebugStubs = DebugStubs.NONE,

    var requestId: RequestId = RequestId.Companion.NONE,
    var timeStart: Instant = Instant.DISTANT_PAST,
    
    // Resume related fields
    var resumeRequest: Resume = Resume(),
    var resumeResponse: Resume = Resume(),
    var resumesResponse: MutableList<Resume> = mutableListOf(),
    
    // Vacancy related fields  
    var vacancyRequest: Vacancy = Vacancy(),
    var vacancyResponse: Vacancy = Vacancy(),
    var vacanciesResponse: MutableList<Vacancy> = mutableListOf(),
    var vacancyFilter: VacancyFilter = VacancyFilter(),
    
    // Pagination
    var paginationOutput: PaginationOutput = PaginationOutput(
        page = 0,
        perPage = 10,
        totalElements = 0,
        hasNext = false
    ),
    var paginationInput: PaginationInput = PaginationInput(
        page = 0,
        perPage = 10
    )
)