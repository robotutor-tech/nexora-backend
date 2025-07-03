package com.robotutor.nexora.automation.exceptions

import com.robotutor.nexora.webClient.exceptions.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0301("NEXORA-0301", "Triggers should not be empty"),
    NEXORA0302("NEXORA-0302", "Few triggers are invalid"),
    NEXORA0303("NEXORA-0303", "Actions should not be empty"),
    NEXORA0304("NEXORA-0304", "Few actions are invalid"),
    NEXORA0305("NEXORA-0305", "Group conditions should not be empty"),
    NEXORA0306("NEXORA-0306", "Condition not found"),
    NEXORA0307("NEXORA-0307", "Few actions are invalid"),
    NEXORA0202("NEXORA-0202", "Invalid credentials"),
    NEXORA0203("NEXORA-0203", "UnAuthorized actor"),
    NEXORA0204("NEXORA-0204", "Invitation not found"),
    NEXORA0205("NEXORA-0205", "Invalid role for authorization"),
}