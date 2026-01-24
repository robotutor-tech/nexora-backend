package com.robotutor.nexora.module.automation.interfaces.controller.view.component.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class AutomationComponentResponse(
    val automationId: String
) : ComponentResponse(ComponentType.AUTOMATION)
