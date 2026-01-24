package com.robotutor.nexora.module.automation.domain.vo.component

import com.robotutor.nexora.module.automation.domain.vo.AutomationId

data class AutomationComponent(val automationId: AutomationId) : Action {
    override val type: ComponentType = ComponentType.AUTOMATION
}