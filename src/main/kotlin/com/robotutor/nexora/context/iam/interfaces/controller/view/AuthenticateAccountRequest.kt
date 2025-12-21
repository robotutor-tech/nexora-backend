package com.robotutor.nexora.context.iam.interfaces.controller.view

import jakarta.validation.constraints.NotBlank

data class AuthenticateAccountRequest(
    @field:NotBlank(message = "CredentialId should not be blank")
    val credentialId: String,
    @field:NotBlank(message = "Credential secret should not be blank")
    val secret: String
)

data class AuthenticateActorRequest(
    @field:NotBlank(message = "PremisesId is required")
    val premisesId: String,
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