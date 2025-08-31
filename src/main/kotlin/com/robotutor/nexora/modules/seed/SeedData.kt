package com.robotutor.nexora.modules.seed


import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.feed.application.command.CreateFeedCommand
import com.robotutor.nexora.modules.feed.domain.model.FeedType
import com.robotutor.nexora.modules.widget.domain.model.WidgetType
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.Name

object SeedData {

    fun getDeviceType(): DeviceType {
        return DeviceType.DEVICE
    }

    fun getCreateFeedCommands(): List<CreateFeedCommand> {
        return listOf(
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 1"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 2"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 3"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 4"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 5"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 6"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 7"),
                WidgetType.TOGGLE,
            ),
            CreateFeedCommand(
                FeedType.ACTUATOR,
                Name("Light 8"),
                WidgetType.TOGGLE,
            ),
        )
    }
}