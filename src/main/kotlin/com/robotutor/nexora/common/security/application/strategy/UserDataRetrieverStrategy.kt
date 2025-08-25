package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.UserDataRetriever
import com.robotutor.nexora.shared.domain.model.UserContext
import com.robotutor.nexora.shared.domain.model.UserData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserDataRetrieverStrategy(
    private val userDataRetriever: UserDataRetriever
) : DataRetrieverStrategy<UserContext, UserData> {
    override fun getPrincipalData(context: UserContext): Mono<UserData> {
        return userDataRetriever.getUserData(context.userId)
    }
}