package com.robotutor.nexora.modules.premises.application.command

import com.robotutor.nexora.modules.premises.domain.entity.Address
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.UserData

data class CreatePremisesCommand(val name: Name, val address: Address, val owner: UserData)
data class RegisterPremisesResourceCommand(val premisesId: PremisesId, val owner: UserData)
