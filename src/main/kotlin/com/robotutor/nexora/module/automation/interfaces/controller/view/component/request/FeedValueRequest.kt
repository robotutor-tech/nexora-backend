package com.robotutor.nexora.module.automation.interfaces.controller.view.component.request

import com.robotutor.nexora.module.automation.domain.vo.component.ComponentType

data class FeedValueRequest(val feedId: String, val value: Int) : ComponentRequest(ComponentType.FEED_VALUE)
