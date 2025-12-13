package com.robotutor.nexora.shared.domain.vo

sealed interface PrincipalData
data class AccountData(val accountId: AccountId, val type: AccountType) :
    PrincipalData

data class ActorData(
    val actorId: ActorId,
    val premisesId: PremisesId,
    val accountId: AccountId,
    val type: AccountType
) : PrincipalData

data class InternalData(val id: String) : PrincipalData
//data class InvitationData(val invitationId: String) : Data

enum class AccountType { HUMAN, MACHINE }
