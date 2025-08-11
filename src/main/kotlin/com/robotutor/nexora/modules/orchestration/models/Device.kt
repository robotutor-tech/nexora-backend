package com.robotutor.nexora.modules.orchestration.models

import com.robotutor.nexora.modules.device.domain.model.DeviceType
import com.robotutor.nexora.modules.feed.models.FeedType
import com.robotutor.nexora.modules.widget.models.WidgetType
import com.robotutor.nexora.modules.zone.models.ZoneId

data class Device(
    val modelNo: String,
    val type: DeviceType,
    val feeds: List<FeedCreationRequest>,
    val feedCount: Int,
    var deviceId: String = "",
) {
    fun updateDeviceId(deviceId: String): Device {
        this.deviceId = deviceId
        return this
    }
}


data class FeedCreationRequest(val feed: Feed, val widget: Widget)

data class Feed(val name: String, val type: FeedType, val index: Int)

data class Widget(val name: String, val type: WidgetType, val zoneId: ZoneId, val data: Map<String, Any> = mapOf())

data class Rule(val triggers: List<Trigger>, val conditions: List<Condition>, val actions: List<String>)

data class Trigger(val type: TriggerType, val data: Map<String, Any>)
enum class TriggerType {
    FEED
}

data class Condition(val operator: String)
