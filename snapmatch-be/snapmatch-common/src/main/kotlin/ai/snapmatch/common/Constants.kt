package ai.snapmatch.common

import kotlinx.datetime.Instant

private val INSTANT_NONE = Instant.fromEpochMilliseconds(Long.MIN_VALUE)
val Instant.Companion.NONE
    get() = INSTANT_NONE

private const val INT_NONE = Int.MIN_VALUE
val Int.Companion.NONE
    get() = INT_NONE