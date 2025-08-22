package com.robotutor.nexora.common.security.application.ports

import com.robotutor.nexora.shared.domain.model.UserData
import com.robotutor.nexora.shared.domain.model.UserId
import reactor.core.publisher.Mono

interface UserDataRetriever {
    fun getUserData(userId: UserId): Mono<UserData>
}