package com.robotutor.nexora.context.premises.application.command

import com.robotutor.nexora.shared.domain.vo.principal.AccountData
import com.robotutor.nexora.context.premises.domain.vo.Address
import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.Name

data class RegisterPremisesCommand(val name: Name, val address: Address, val owner: AccountData) : Command
