package com.robotutor.nexora.shared.domain.event

import com.robotutor.nexora.modules.feed.domain.model.FeedType
import com.robotutor.nexora.modules.widget.domain.model.WidgetType
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.model.ModelNo
import com.robotutor.nexora.shared.domain.model.Name
import com.robotutor.nexora.shared.domain.model.ResourceId
import com.robotutor.nexora.shared.domain.model.ResourceType
import com.robotutor.nexora.shared.domain.model.UserId
import com.robotutor.nexora.shared.domain.model.ZoneId
import java.time.Instant
import java.util.*

open class DomainEvent {
    val id: String = UUID.randomUUID().toString()
    val occurredOn: Instant = Instant.now()
    val eventName: Name = Name(this::class.simpleName ?: "UnknownEvent")
}

data class ResourceCreatedEvent(
    val resourceType: ResourceType,
    val resourceId: ResourceId,
) : DomainEvent()

data class UserRegisteredEvent(
    val userId: UserId,
) : DomainEvent()


data class DeviceCreatedEvent(
    val deviceId: DeviceId,
    val modelNo: ModelNo,
    val zoneId: ZoneId,
) : DomainEvent()

data class FeedCreatedEvent(
    val feedId: FeedId,
    val name: Name,
    val type: FeedType,
    val widgetType: WidgetType,
    val zoneId: ZoneId,
) : DomainEvent()

data class DeviceFeedsCreatedEvent(
    val deviceId: DeviceId,
    val feedIds: List<FeedId>,
) : DomainEvent()
