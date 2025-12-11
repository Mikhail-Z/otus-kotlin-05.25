package ai.snapmatch.app.common.api

import ai.snapmatch.common.Context

interface ISnapmatchProcessor {
    suspend fun exec(ctx: Context)
}