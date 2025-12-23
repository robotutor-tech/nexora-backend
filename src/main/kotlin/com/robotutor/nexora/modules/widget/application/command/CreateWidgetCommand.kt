package com.robotutor.nexora.modules.widget.application.command

import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.ZoneId

data class CreateWidgetCommand(val name: Name, val feedId: FeedId, val zoneId: ZoneId, val widgetType: WidgetType)