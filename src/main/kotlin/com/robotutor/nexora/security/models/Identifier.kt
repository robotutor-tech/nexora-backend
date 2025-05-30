package com.robotutor.nexora.security.models

data class Identifier<T>(val id: String, val type: T)

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