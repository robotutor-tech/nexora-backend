package com.robotutor.nexora.orchestration.models

import com.robotutor.nexora.device.models.DeviceType
import com.robotutor.nexora.widget.models.WidgetType

data class Device(
    val modelNo: String,
    val type: DeviceType,
    val feeds: List<Feed>,
    val widgets: List<Widget>,
    val rules: List<Rule>
)

data class Feed(val name: String, val type: FeedType)
enum class FeedType {
    SENSOR,
    ACTUATOR
}

data class Widget(val name: String, val type: WidgetType, val data: Map<String, Any> = mapOf()) {
}

data class Rule(val triggers: List<Trigger>, val conditions: List<Condition>, val actions: List<Action>)

data class Trigger(val type: TriggerType, val data: Map<String, Any>)
enum class TriggerType {
    FEED
}

data class Condition(val operator: String)
data class Action(
    val type: ActionType, val data: Map<String, Any>
)

enum class ActionType {
    FEED
}
