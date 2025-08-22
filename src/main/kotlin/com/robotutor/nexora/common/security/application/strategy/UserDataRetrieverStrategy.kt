package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.UserDataRetriever
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.PrincipalData
import com.robotutor.nexora.shared.domain.model.UserContext
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDataRetrieverStrategy(private val userDataRetriever: UserDataRetriever) : DataRetrieverStrategy {
    override fun getPrincipalData(principalContext: PrincipalContext): Mono<PrincipalData> {
        return userDataRetriever.getUserData((principalContext as UserContext).userId)
            .map { it as PrincipalData }
    }
}