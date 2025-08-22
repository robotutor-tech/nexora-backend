package com.robotutor.nexora.modules.auth.interfaces.controller.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank


data class AuthLoginRequest(
    @field:Email(message = "Email should be valid")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class ActorLoginRequest(
    @field:NotBlank(message = "Actor id is required")
    val actorId: String,
    @field:NotBlank(message = "Role id is required")
    val roleId: String
)


//data class PremisesActorDataView(
//    val actorId: ActorId,
//    val role: RoleWithPolicyTypeView,
//    val premisesId: String,
//    val identifier: Identifier<ActorIdentifier>
//) : IAuthenticationData {
//    companion object {
//        fun from(premisesActorData: PremisesActorData): PremisesActorDataView {
//            return PremisesActorDataView(
//                actorId = premisesActorData.actorId,
//                role = RoleWithPolicyTypeView.from(premisesActorData.roleDocument),
//                premisesId = premisesActorData.premisesId,
//                identifier = premisesActorData.identifier
//            )
//        }
//    }
//}

//data class RoleWithPolicyTypeView(
//    val roleId: RoleId,
//    val premisesId: String,
//    val name: String,
//    val role: RoleType,
//) {
//    companion object {
//        fun from(roleDocument: RoleDocument): RoleWithPolicyTypeView {
//            return RoleWithPolicyTypeView(
//                roleId = roleDocument.roleId,
//                premisesId = roleDocument.premisesId,
//                name = roleDocument.name,
//                role = roleDocument.role,
//            )
//        }
//    }
//}