package ai.snapmatch.app.common.api

interface IWsSessionRepo {
    suspend fun add(session: IWsSession)
    suspend fun remove(session: IWsSession)
    suspend fun getSessions(userId: String): List<IWsSession>
    suspend fun <T> sendToUser(userId: String, obj: T)

    companion object {
        val NONE = object : IWsSessionRepo {
            override suspend fun add(session: IWsSession) {}
            override suspend fun remove(session: IWsSession) {}
            override suspend fun getSessions(userId: String) = emptyList<IWsSession>()
            override suspend fun <T> sendToUser(userId: String, obj: T) {}
        }
    }
}

// Обертка над WebSocketSession
interface IWsSession {
    val userId: String
    suspend fun <T> send(obj: T)
}