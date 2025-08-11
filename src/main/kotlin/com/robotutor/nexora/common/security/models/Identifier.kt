package com.robotutor.nexora.common.security.models

data class Identifier<T>(val id: String, val type: T) {
    fun isValid(id: String?, type: T) = this.type == type && this.id == id
}

enum class ActorIdentifier {
    USER,
    DEVICE,
    LOCAL_SERVER,
    SERVER
}

enum class TokenIdentifier {
    AUTH_USER,
    PREMISES_ACTOR,
    INVITATION,
}

enum class InvitationIdentifier {
    DEVICE,
    USER
}

typealias DeviceId = String