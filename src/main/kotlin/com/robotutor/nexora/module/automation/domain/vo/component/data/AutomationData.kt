package com.robotutor.nexora.module.automation.domain.vo.component.data

import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.component.AutomationComponent

data class AutomationData(
    val automationId: AutomationId,
) : ComponentData<AutomationComponent>