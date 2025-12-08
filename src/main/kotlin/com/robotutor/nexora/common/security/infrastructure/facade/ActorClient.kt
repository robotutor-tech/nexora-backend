//package com.robotutor.nexora.common.security.infrastructure.facade
//
//import com.robotutor.nexora.common.security.application.ports.ActorDataRetriever
//import com.robotutor.nexora.common.security.application.ports.ActorResponse
//import com.robotutor.nexora.modules.iam.interfaces.controller.ActorController
//import com.robotutor.nexora.shared.domain.model.ActorId
//import com.robotutor.nexora.shared.domain.model.PremisesId
//import com.robotutor.nexora.shared.domain.model.Role
//import com.robotutor.nexora.shared.domain.model.RoleId
//import com.robotutor.nexora.shared.domain.vo.Name
//import com.robotutor.nexora.shared.interfaces.mapper.PrincipalContextMapper
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service("SecurityActorClient")
//class ActorClient(private val actorController: ActorController) : ActorDataRetriever {
//    override fun getActorData(actorId: ActorId, roleId: RoleId): Mono<ActorResponse> {
//        return actorController.getActor(actorId.value, roleId.value)
//            .switchIfEmpty(Mono.error(RuntimeException("Actor not found")))
//            .map {
//                val role = Role(
//                    roleId = RoleId(it.role.roleId),
//                    premisesId = PremisesId(it.role.premisesId),
//                    name = Name(it.role.name),
//                    roleType = it.role.roleType
//                )
//                ActorResponse(
//                    actorId = ActorId(it.actorId),
//                    role = role,
//                    premisesId = PremisesId(it.premisesId),
//                    principalType = it.principalType,
//                    principal = PrincipalContextMapper.toActorPrincipalContext(it.principal)
//                )
//            }
//    }
//}
