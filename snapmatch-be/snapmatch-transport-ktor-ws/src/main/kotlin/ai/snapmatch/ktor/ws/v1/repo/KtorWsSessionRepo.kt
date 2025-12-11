package ai.snapmatch.transport.ktor.ws.ai.snapmatch.ktor.ws.v1.repo

import ai.snapmatch.api.v1.models.IResponseDto
import ai.snapmatch.app.common.api.IWsSession
import ai.snapmatch.app.common.api.IWsSessionRepo
import ai.snapmatch.api.v1.jackson.apiV1ResponseSerialize
import kotlinx.coroutines.channels.ClosedSendChannelException
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import io.ktor.websocket.*

class KtorWsSessionRepo : IWsSessionRepo {
    private val logger = LoggerFactory.getLogger(this::class.java)

    // userId -> Set<WsSession>
    private val sessions = ConcurrentHashMap<String, MutableSet<IWsSession>>()

    override suspend fun add(session: IWsSession) {
        sessions.compute(session.userId) { _, existingSessions ->
            (existingSessions ?: mutableSetOf()).apply {
                add(session)
            }
        }
        logger.info("WebSocket session added for user: ${session.userId}, total: ${sessions[session.userId]?.size}")
    }

    override suspend fun remove(session: IWsSession) {
        sessions.compute(session.userId) { _, existingSessions ->
            existingSessions?.apply {
                remove(session)
            }?.takeIf { it.isNotEmpty() } // удаляем ключ если set пустой
        }
        logger.info("WebSocket session removed for user: ${session.userId}")
    }

    override suspend fun getSessions(userId: String): List<IWsSession> {
        return sessions[userId]?.toList() ?: emptyList()
    }

    override suspend fun <T> sendToUser(userId: String, obj: T) {
        val userSessions = sessions[userId] ?: return

        logger.debug("Sending message to user $userId (${userSessions.size} sessions)")

        val deadSessions = mutableListOf<IWsSession>()

        for (session in userSessions) {
            try {
                session.send(obj)
            } catch (e: ClosedSendChannelException) {
                logger.warn("Session closed for user $userId, will be removed")
                deadSessions.add(session)
            } catch (e: Exception) {
                logger.error("Error sending message to user $userId", e)
                deadSessions.add(session)
            }
        }

        // Очистка мертвых сессий
        deadSessions.forEach { remove(it) }
    }
}

// Адаптер Ktor WebSocketSession -> WsSession
class KtorWsSession(
    private val webSocketSession: WebSocketSession,
    override val userId: String
) : IWsSession {

    override suspend fun <T> send(obj: T) {
        require(obj is IResponseDto)
        val message = apiV1ResponseSerialize(obj)
        webSocketSession.send(Frame.Text(message))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KtorWsSession) return false
        return webSocketSession == other.webSocketSession
    }

    override fun hashCode(): Int {
        return webSocketSession.hashCode()
    }
}