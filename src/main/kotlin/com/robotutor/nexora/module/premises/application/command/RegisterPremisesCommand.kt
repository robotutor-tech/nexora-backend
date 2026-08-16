package com.robotutor.nexora.module.premises.application.command

import com.robotutor.nexora.module.premises.domain.vo.Address
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.UserData

data class RegisterPremisesCommand(val name: Name, val address: Address, val owner: UserData) : Command {
    fun toMetaData(): Map<String, Any?> {
        return mapOf(
            "name" to name.value,
            "address" to mapOf(
                "street" to address.street,
                "city" to address.city,
                "state" to address.state,
                "country" to address.country,
                "postalCode" to address.postalCode,
            ),
            "owner" to owner.accountId.value
        )
    }
}
