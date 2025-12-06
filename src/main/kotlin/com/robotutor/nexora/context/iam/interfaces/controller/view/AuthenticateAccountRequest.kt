package com.robotutor.nexora.context.iam.interfaces.controller.view

import com.robotutor.nexora.context.iam.domain.vo.CredentialKind
import jakarta.validation.constraints.NotBlank

data class AuthenticateAccountRequest(
    @field:NotBlank(message = "CredentialId should not be blank")
    val credentialId: String,
    @field:NotBlank(message = "Credential secret should not be blank")
    val secret: String,
    val kind: CredentialKind
)

data class ActorLoginRequest(
    @field:NotBlank(message = "Actor id is required")
    val actorId: String,
    @field:NotBlank(message = "Role id is required")
    val roleId: String
)

data class DeviceLoginRequest(
    @field:NotBlank(message = "Device id is required")
    val deviceId: String,
    @field:NotBlank(message = "Secret id is required")
    val secret: String
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