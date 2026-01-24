package com.robotutor.nexora.module.automation.infrastructure.persistence.document.component

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

sealed class ComponentDocument(val type: ComponentType)

