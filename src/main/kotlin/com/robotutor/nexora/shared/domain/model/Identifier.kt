package com.robotutor.nexora.shared.domain.model

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
    USER,
    ACTOR,
    SERVER,
    INVITATION,
    INTERNAL,
}