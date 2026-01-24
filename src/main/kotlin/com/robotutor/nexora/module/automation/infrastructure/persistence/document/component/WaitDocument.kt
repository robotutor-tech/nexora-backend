package com.robotutor.nexora.module.automation.infrastructure.persistence.document.component

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType
import org.springframework.data.annotation.TypeAlias

@TypeAlias("WAIT")
data class WaitDocument(val duration: Long) : ComponentDocument(ComponentType.WAIT)
