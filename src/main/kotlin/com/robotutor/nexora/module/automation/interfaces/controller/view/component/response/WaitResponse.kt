package com.robotutor.nexora.module.automation.interfaces.controller.view.component.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class WaitResponse(
    val duration: Long
) : ComponentResponse(ComponentType.WAIT)
