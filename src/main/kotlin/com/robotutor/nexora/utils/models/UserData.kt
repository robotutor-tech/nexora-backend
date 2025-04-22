package com.robotutor.nexora.utils.models

import com.robotutor.nexora.premises.models.PremisesId
import com.robotutor.nexora.security.gateway.view.AuthenticationResponseData

interface IAuthenticationData

data class UserData(val userId: UserId, val premisesId: PremisesId? = null) : IAuthenticationData {
    companion object {
        fun from(authenticationResponseData: AuthenticationResponseData): UserData {
            return UserData(userId = authenticationResponseData.userId)
        }
    }
}

data class UserPremisesData(val userId: UserId, val premisesId: PremisesId) : IAuthenticationData {
    companion object {
        fun from(authenticationData: AuthenticationResponseData): UserPremisesData {
            if (authenticationData.premisesId == null) {
                throw Exception("User premises not found") // TODO update this exception with relevant data
            }
            return UserPremisesData(userId = authenticationData.userId, premisesId = authenticationData.premisesId)
        }
    }
}

typealias UserId = String
