package com.robotutor.nexora.module.automation.interfaces.controller.view.component.request

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class WaitRequest(val duration: Int) : ComponentRequest(ComponentType.WAIT)
