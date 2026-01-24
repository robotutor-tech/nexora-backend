package com.robotutor.nexora.module.automation.interfaces.controller.view.component.response

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

sealed class ComponentResponse(val type: ComponentType)