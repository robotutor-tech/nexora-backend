package com.robotutor.nexora.common.security.application.strategy

import com.robotutor.nexora.common.security.application.ports.InvitationDataRetriever
import com.robotutor.nexora.shared.domain.model.InvitationContext
import com.robotutor.nexora.shared.domain.model.InvitationData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class InvitationDataRetrieverStrategy(
    private val invitationDataRetriever: InvitationDataRetriever
) : DataRetrieverStrategy<InvitationContext, InvitationData> {
    override fun getPrincipalData(context: InvitationContext): Mono<InvitationData> {
        return invitationDataRetriever.getInvitationData(context.value)
    }
}