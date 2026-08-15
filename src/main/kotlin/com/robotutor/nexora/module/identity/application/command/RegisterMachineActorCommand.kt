package com.robotutor.nexora.module.identity.application.command

import com.robotutor.nexora.shared.application.command.Command
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ResourceId

data class RegisterMachineActorCommand(val premisesId: PremisesId, val owner: DeviceData, val deviceId: ResourceId) :
    Command
