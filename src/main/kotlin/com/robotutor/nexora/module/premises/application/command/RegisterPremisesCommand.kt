package com.robotutor.nexora.module.premises.application.command

import com.robotutor.nexora.module.premises.domain.vo.Address
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.principal.UserData

data class RegisterPremisesCommand(val name: Name, val address: Address, val owner: UserData) : Command
