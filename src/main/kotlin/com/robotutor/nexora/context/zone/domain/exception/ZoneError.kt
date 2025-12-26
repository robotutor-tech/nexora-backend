package com.robotutor.nexora.context.zone.domain.exception

import com.robotutor.nexora.shared.domain.exception.ServiceError

enum class ZoneError(override val errorCode: String, override val message: String) : ServiceError {
    NEXORA0201("NEXORA-0201", "Zone creation denied"),
    NEXORA0202("NEXORA-0202", "Widget creation denied"),
    NEXORA0203("NEXORA-0203", "Registered user must not have accountId."),
    NEXORA0204("NEXORA-0204", "User is not in registered state."),
    NEXORA0205("NEXORA-0205", "User not found!!."),

}