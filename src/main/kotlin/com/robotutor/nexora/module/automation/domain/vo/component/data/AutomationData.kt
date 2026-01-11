package com.robotutor.nexora.module.automation.domain.vo.component.data

import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.automation.domain.vo.component.Action
import com.robotutor.nexora.module.automation.domain.vo.component.Automation

data class AutomationData(
    val automationId: AutomationId,
) : ComponentData<Automation>