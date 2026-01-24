package com.robotutor.nexora.module.automation.interfaces.controller.view.component.request

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class VoiceRequest(val commands: List<String>) : ComponentRequest(ComponentType.VOICE)
