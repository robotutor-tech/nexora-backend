package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.UserDataRetriever
import com.robotutor.nexora.shared.domain.model.PrincipalData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDataRetrieverStrategy(private val userDataRetriever: UserDataRetriever) : DataRetrieverStrategy {
    override fun getPrincipalData(principalId: String): Mono<PrincipalData> {
        return userDataRetriever.getUserData(principalId)
            .map { it as PrincipalData }
    }
}