package com.robotutor.nexora.modules.widget.interfaces.messaging.mapper

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.modules.widget.interfaces.messaging.message.CreateWidgetMessage
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.model.ZoneId

object WidgetMapper {
    fun toCreateWidgetCommand(message: CreateWidgetMessage): CreateWidgetCommand {
        return CreateWidgetCommand(
            name = Name(message.name),
            feedId = FeedId(message.feedId),
            zoneId = ZoneId(message.zoneId),
            widgetType = message.widgetType
        )
    }
}