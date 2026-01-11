package com.robotutor.nexora.shared.domain.vo

import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import com.robotutor.nexora.module.device.domain.vo.DeviceId
import com.robotutor.nexora.module.zone.domain.vo.WidgetId

enum class ResourceType(val identifier: Class<out Identifier>) {
    AUTOMATION(AutomationId::class.java),
    DEVICE(DeviceId::class.java),
    FEED(FeedId::class.java),
    PREMISES(PremisesId::class.java),
    WIDGET(WidgetId::class.java),
    ZONE(ZoneId::class.java),
}

enum class ActionType {
    CREATE,
    READ,
    CONTROL,
    UPDATE,
    DELETE,
}