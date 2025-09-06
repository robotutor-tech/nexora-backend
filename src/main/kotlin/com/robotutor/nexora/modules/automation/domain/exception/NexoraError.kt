package com.robotutor.nexora.modules.automation.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError


enum class NexoraError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0301("NEXORA-0301", "Invalid config type"),
    NEXORA0302("NEXORA-0302", "Few triggers are invalid"),
    NEXORA0303("NEXORA-0303", "Actions should not be empty"),
    NEXORA0304("NEXORA-0304", "Few actions are invalid"),
    NEXORA0305("NEXORA-0305", "Invalid automation condition"),
    NEXORA0306("NEXORA-0306", "Condition not found"),
    NEXORA0307("NEXORA-0307", "Invalid trigger config"),
    NEXORA0308("NEXORA-0308", "Invalid action config"),
    NEXORA0309("NEXORA-0309", "Invalid condition config"),
    NEXORA0310("NEXORA-0310", "Trigger already exists"),
    NEXORA0311("NEXORA-0311", "Action already exists"),
    NEXORA0312("NEXORA-0312", "Condition already exists"),
    NEXORA0313("NEXORA-0313", "Trigger not found."),
    NEXORA0314("NEXORA-0314", "Too many nested conditions."),
    NEXORA0315("NEXORA-0315", "Condition not found."),
    NEXORA0316("NEXORA-0316", "Action not found."),
}