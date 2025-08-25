package com.robotutor.nexora.modules.widget.application.command

import com.robotutor.nexora.modules.widget.domain.model.WidgetType
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ZoneId

data class CreateWidgetCommand(val name: Name, val feedId: FeedId, val zoneId: ZoneId, val widgetType: WidgetType)