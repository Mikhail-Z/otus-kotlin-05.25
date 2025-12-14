package ai.snapmatch.common.models

data class PaginationInput(
    val page: Int,
    val perPage: Int
)

data class PaginationOutput(
    val page: Int,
    val perPage: Int,
    val totalElements: Int,
    val hasNext: Boolean
)