package com.robotutor.nexora.common.security.application.strategy.factory

import com.robotutor.nexora.common.security.application.strategy.AccountDataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.ActorDataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.DataRetrieverStrategy
import com.robotutor.nexora.common.security.application.strategy.InternalDataRetrieverStrategy
import com.robotutor.nexora.common.security.domain.vo.AccountPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.ActorPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.InternalPrincipalContext
import com.robotutor.nexora.common.security.domain.vo.PrincipalContext
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import org.springframework.stereotype.Service

@Service
class TokenDataRetrieverStrategyFactory(
    private val accountDataRetrieverStrategy: AccountDataRetrieverStrategy,
    private val internalDataRetrieverStrategy: InternalDataRetrieverStrategy,
    private val actorDataRetrieverStrategy: ActorDataRetrieverStrategy,
//    private val invitationDataRetrieverStrategy: InvitationDataRetrieverStrategy,
) {
    fun getStrategy(principalContext: PrincipalContext): DataRetrieverStrategy<PrincipalContext, PrincipalData> {
        @Suppress("UNCHECKED_CAST")
        return when (principalContext) {
            is AccountPrincipalContext -> accountDataRetrieverStrategy
            is ActorPrincipalContext -> actorDataRetrieverStrategy
            is InternalPrincipalContext -> internalDataRetrieverStrategy
        } as DataRetrieverStrategy<PrincipalContext, PrincipalData>
    }
}