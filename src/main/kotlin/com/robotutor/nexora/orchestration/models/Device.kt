package com.robotutor.nexora.orchestration.models

import com.robotutor.nexora.device.models.DeviceId
import com.robotutor.nexora.device.models.DeviceType
import com.robotutor.nexora.feed.models.FeedType
import com.robotutor.nexora.widget.models.WidgetType
import com.robotutor.nexora.zone.models.ZoneId

data class Device(
    val modelNo: String,
    val type: DeviceType,
    val feeds: List<FeedCreationRequest>,
    val feedCount: Int,
    var deviceId: DeviceId = "",
) {
    fun updateDeviceId(deviceId: DeviceId): Device {
        this.deviceId = deviceId
        return this
    }
}

data class Policy(val name: String, val access: AccessType)
enum class AccessType {
    READ,
    UPDATE
}

data class FeedCreationRequest(val feed: Feed, val widget: Widget, val policies: List<Policy>)

data class Feed(val name: String, val type: FeedType, val index: Int)

data class Widget(val name: String, val type: WidgetType, val zoneId: ZoneId, val data: Map<String, Any> = mapOf())

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
