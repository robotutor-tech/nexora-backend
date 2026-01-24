package com.robotutor.nexora.module.automation.infrastructure.persistence.document.component

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class VoiceDocument(val commands: List<String>) : ComponentDocument(ComponentType.VOICE)
