package ai.snapmatch.app.common

import ai.snapmatch.app.common.api.IAppSettings
import ai.snapmatch.common.Context
import ai.snapmatch.common.helpers.asSnapmatchError
import ai.snapmatch.common.models.Command
import ai.snapmatch.common.models.State
import kotlin.reflect.KClass

suspend inline fun <T> IAppSettings.processRequest(
    crossinline getRequest: suspend Context.() -> Unit,
    crossinline toResponse: suspend Context.() -> T,
    clazz: KClass<*>,
): T {
    val ctx = Context()
    return try {
        ctx.getRequest()
        
        processor.exec(ctx)
        
        ctx.toResponse()
    } catch (e: Throwable) {
        ctx.state = State.FAILING
        ctx.errors.add(e.asSnapmatchError())
        processor.exec(ctx)
        if (ctx.command == Command.NONE) {
            ctx.command = Command.GET_RESUME
        }
        ctx.toResponse()
    }
}