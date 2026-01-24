package com.robotutor.nexora.module.automation.application.command

import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Resources

data class GetAutomationsQuery(val resources: Resources, val actorId: ActorId)
