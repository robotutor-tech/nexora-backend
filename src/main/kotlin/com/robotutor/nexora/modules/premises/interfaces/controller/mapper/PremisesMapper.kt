package com.robotutor.nexora.modules.premises.interfaces.controller.mapper

import com.robotutor.nexora.modules.premises.application.dto.ActorWithRolesPremises
import com.robotutor.nexora.modules.premises.application.facade.dto.ActorWithRoles
import com.robotutor.nexora.modules.premises.application.facade.dto.Role
import com.robotutor.nexora.modules.premises.domain.entity.Premises
import com.robotutor.nexora.modules.premises.domain.entity.Address
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.ActorWithRoleResponse
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.PremisesActorResponse
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.PremisesResponse
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.RoleResponse
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.AddressRequest
import com.robotutor.nexora.modules.premises.interfaces.controller.dto.AddressResponse

class PremisesMapper {
    companion object {
        fun toPremisesActorResponse(actorWithRolesPremises: ActorWithRolesPremises): PremisesActorResponse {
            val premises = actorWithRolesPremises.premises
            val actor = actorWithRolesPremises.actor
            return PremisesActorResponse(
                premisesId = premises.premisesId.value,
                name = premises.name.value,
                address = toAddressResponse(premises.address),
                createdAt = premises.createdAt,
                actor = toActorResponse(actor)
            )
        }

        private fun toActorResponse(actor: ActorWithRoles): ActorWithRoleResponse {
            return ActorWithRoleResponse(
                actorId = actor.actorId.value,
                premisesId = actor.premisesId.value,
                roles = actor.roles.map { toRoleResponse(it) },
            )
        }

        fun toRoleResponse(role: Role): RoleResponse {
            return RoleResponse(
                roleId = role.roleId.value,
                name = role.name.value,
                roleType = role.roleType,
            )
        }

        fun toPremisesResponse(premises: Premises): PremisesResponse {
            return PremisesResponse(
                premisesId = premises.premisesId.value,
                name = premises.name.value,
                address = toAddressResponse(premises.address),
                createdAt = premises.createdAt
            )
        }

        private fun toAddressResponse(address: Address): AddressResponse {
            return AddressResponse(
                street = address.street,
                city = address.city,
                state = address.state,
                country = address.country,
                postalCode = address.postalCode
            )
        }

        fun toAddress(addressRequest: AddressRequest): Address {
            return Address(
                street = addressRequest.street,
                city = addressRequest.city,
                state = addressRequest.state,
                country = addressRequest.country,
                postalCode = addressRequest.postalCode
            )
        }
    }
}