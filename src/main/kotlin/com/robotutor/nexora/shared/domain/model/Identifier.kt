package com.robotutor.nexora.shared.domain.model

data class Identifier<T>(val id: String, val type: T) {
    fun isValid(id: String?, type: T) = this.type == type && this.id == id
}

enum class ActorPrincipalType {
    USER,
    DEVICE,
}

enum class TokenPrincipalType {
    USER,
    ACTOR,
    INVITATION,
    INTERNAL,
}