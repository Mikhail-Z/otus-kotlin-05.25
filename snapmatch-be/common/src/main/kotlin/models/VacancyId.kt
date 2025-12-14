package ai.snapmatch.common.models

import java.util.UUID

@JvmInline
value class VacancyId(private val id: String) {
    init {
        require(id.isEmpty() || id == "NONE" || isValidUUID(id)) { 
            "Invalid UUID format: $id" 
        }
    }
    
    fun asString() = id
    fun asUUID() = if (isEmpty()) null else UUID.fromString(id)
    fun isEmpty() = id.isEmpty() || id == "NONE"
    
    companion object {
        val NONE = VacancyId("NONE")
        
        private fun isValidUUID(str: String): Boolean = 
            runCatching { UUID.fromString(str) }.isSuccess
    }
}