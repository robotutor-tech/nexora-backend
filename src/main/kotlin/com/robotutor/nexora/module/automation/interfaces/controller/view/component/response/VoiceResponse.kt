package com.robotutor.nexora.module.automation.interfaces.controller.view.component.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class VoiceResponse(
    val commands: List<String>
) : ComponentResponse(ComponentType.VOICE)

