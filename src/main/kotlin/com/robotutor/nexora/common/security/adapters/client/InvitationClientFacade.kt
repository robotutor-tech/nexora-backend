package com.robotutor.nexora.common.security.adapters.client

import com.robotutor.nexora.common.security.application.ports.InvitationDataRetriever
import com.robotutor.nexora.modules.auth.interfaces.controller.InvitationController
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.InvitationData
import com.robotutor.nexora.shared.domain.model.InvitationId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.ZoneId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service("SecurityInvitationClientFacade")
class InvitationClientFacade(private val invitationController: InvitationController) : InvitationDataRetriever {
    override fun getInvitationData(invitationId: InvitationId): Mono<InvitationData> {
        return invitationController.getInvitation(invitationId.value)
            .map {
                InvitationData(
                    invitationId = InvitationId(it.invitationId),
                    premisesId = PremisesId(it.premisesId),
                    name = Name(it.name),
                    zoneId = ZoneId(it.zoneId),
                    invitedBy = ActorId(it.invitedBy)
                )
            }
    }
}