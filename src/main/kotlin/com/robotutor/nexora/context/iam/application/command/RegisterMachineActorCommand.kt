package com.robotutor.nexora.context.iam.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId

data class RegisterMachineActorCommand(val premisesId: PremisesId, val owner: AccountData, val deviceId: ResourceId) :
    Command
