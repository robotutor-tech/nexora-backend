package com.robotutor.nexora.common.security.infrastructure.facade

import com.robotutor.nexora.common.security.application.ports.InvitationDataRetriever
import com.robotutor.nexora.context.iam.interfaces.controller.InvitationController
import com.robotutor.nexora.shared.domain.model.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityInvitationClientFacade")
class InvitationClientFacade(private val invitationController: InvitationController) : InvitationDataRetriever {
    override fun getInvitationData(invitationId: InvitationId): Mono<InvitationData> {
        return Mono.empty()
//        return invitationController.getInvitation(invitationId.value)
//            .map {
//                InvitationData(
//                    invitationId = InvitationId(it.invitationId),
//                    premisesId = PremisesId(it.premisesId),
//                    name = Name(it.name),
//                    zoneId = ZoneId(it.zoneId),
//                    invitedBy = ActorId(it.invitedBy)
//                )
//            }
    }
}
