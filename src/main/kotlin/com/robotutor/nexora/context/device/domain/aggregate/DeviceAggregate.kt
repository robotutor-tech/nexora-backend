package com.robotutor.nexora.context.device.domain.aggregate

import com.robotutor.nexora.context.device.domain.event.DeviceActivatedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceEvent
import com.robotutor.nexora.context.device.domain.event.DeviceMetadataUpdatedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceRegisteredEvent
import com.robotutor.nexora.context.device.domain.exception.DeviceError
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.device.domain.vo.SerialNo
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.vo.*
import java.time.Instant

class DeviceAggregate private constructor(
    val deviceId: DeviceId,
    val premisesId: PremisesId,
    val zoneId: ZoneId,
    val registeredBy: ActorId,
    val createdAt: Instant,
    private var name: Name,
    private var metadata: DeviceMetadata?,
    private var state: DeviceState,
    private var feedIds: Set<FeedId>,
    private var health: DeviceHealth,
    private var updatedAt: Instant,
) : AggregateRoot<DeviceAggregate, DeviceId, DeviceEvent>(deviceId) {

    fun getHealth(): DeviceHealth = health
    fun getState(): DeviceState = state
    fun getFeedIds(): Set<FeedId> = feedIds
    fun getUpdatedAt(): Instant = updatedAt
    fun getName(): Name = name
    fun getMetadata(): DeviceMetadata? = metadata

    fun commission(metadata: DeviceMetadata, feedIds: Set<FeedId>): DeviceAggregate {
        if (state != DeviceState.REGISTERED) {
            throw InvalidStateException(DeviceError.NEXORA0401)
        }
        this.metadata = metadata
        this.feedIds = feedIds
        this.state = DeviceState.ACTIVE
        this.updatedAt = Instant.now()
        addEvent(DeviceMetadataUpdatedEvent(deviceId, metadata))
        return this
    }


    companion object {
        fun register(
            premisesId: PremisesId,
            name: Name,
            registeredBy: ActorId,
            zoneId: ZoneId,
        ): DeviceAggregate {
            val device = create(
                deviceId = DeviceId.generate(),
                premisesId = premisesId,
                name = name,
                registeredBy = registeredBy,
                zoneId = zoneId
            )
            device.addEvent(DeviceRegisteredEvent(device.deviceId, device.name, device.premisesId))
            return device
        }

        fun create(
            deviceId: DeviceId,
            premisesId: PremisesId,
            name: Name,
            zoneId: ZoneId,
            registeredBy: ActorId,
            feedIds: Set<FeedId> = emptySet(),
            state: DeviceState = DeviceState.CREATED,
            health: DeviceHealth = DeviceHealth.OFFLINE,
            metaData: DeviceMetadata? = null,
            createdAt: Instant = Instant.now(),
            updatedAt: Instant = Instant.now(),
        ): DeviceAggregate {
            return DeviceAggregate(
                deviceId = deviceId,
                premisesId = premisesId,
                name = name,
                feedIds = feedIds,
                state = state,
                health = health,
                metadata = metaData,
                zoneId = zoneId,
                registeredBy = registeredBy,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

    fun actorRegistered(): DeviceAggregate {
        if (state != DeviceState.CREATED) {
            throw InvalidStateException(DeviceError.NEXORA0403)
        }
        state = DeviceState.REGISTERED
        updatedAt = Instant.now()
        addEvent(DeviceActivatedEvent(deviceId, premisesId))
        return this
    }
}

enum class DeviceState {
    CREATED,
    REGISTERED,
    ACTIVE,
    INACTIVE,
}

enum class DeviceHealth {
    ONLINE,
    OFFLINE,
}

data class DeviceMetadata(
    val osName: Name,
    val osVersion: Name,
    val modelNo: ModelNo,
    val serialNo: SerialNo,
)
