package ai.snapmatch.common.models

enum class ResumeProcessingStatus {
    UPLOADED,
    PROCESSING,
    ACCEPTED,
    REJECTED
}

enum class UserRole {
    CANDIDATE,
    HR_SPECIALIST
}

enum class LLMProvider {
    GIGACHAT
}