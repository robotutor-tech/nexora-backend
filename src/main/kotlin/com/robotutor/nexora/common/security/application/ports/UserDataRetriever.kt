package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.domain.model.UserData
import reactor.core.publisher.Mono

interface UserDataRetriever {
    fun getUserData(userId: String): Mono<UserData>
}