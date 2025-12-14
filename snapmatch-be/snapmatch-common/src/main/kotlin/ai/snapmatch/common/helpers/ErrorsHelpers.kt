package ai.snapmatch.common.helpers

import ai.snapmatch.common.models.SnapmatchError

fun Throwable.asSnapmatchError(
    code: String = "unknown",
    group: String = "ai/snapmatch/common/exceptionsch/common/exceptions",
    message: String = this.message ?: "",
) = SnapmatchError(
    code = code,
    group = group,
    field = "",
    message = message,
    exception = this,
)