package com.robotutor.nexora.common.security.application.strategy.factory

import com.robotutor.nexora.common.security.application.strategy.ActorDataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.DataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.InternalUserDataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.InvitationDataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.UserDataRetrieverStrategy
import com.robotutor.nexora.shared.domain.model.PrincipalContext
import com.robotutor.nexora.shared.domain.model.PrincipalData
import com.robotutor.nexora.shared.domain.model.TokenPrincipalType
import org.springframework.stereotype.Service

@Service
class TokenDataRetrieverStrategyFactory(
    private val userDataRetrieverStrategy: UserDataRetrieverStrategy,
    private val internalUserDataRetrieverStrategy: InternalUserDataRetrieverStrategy,
    private val actorDataRetrieverStrategy: ActorDataRetrieverStrategy,
    private val invitationDataRetrieverStrategy: InvitationDataRetrieverStrategy,
) {
    fun getStrategy(tokenPrincipalType: TokenPrincipalType): DataRetrieverStrategy<PrincipalContext, PrincipalData> {
        @Suppress("UNCHECKED_CAST")
        return when (tokenPrincipalType) {
            TokenPrincipalType.USER -> userDataRetrieverStrategy
            TokenPrincipalType.INTERNAL -> internalUserDataRetrieverStrategy
            TokenPrincipalType.ACTOR -> actorDataRetrieverStrategy
            TokenPrincipalType.INVITATION -> invitationDataRetrieverStrategy
        } as DataRetrieverStrategy<PrincipalContext, PrincipalData>
    }
}