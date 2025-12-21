package com.robotutor.nexora.context.device.domain.aggregate

import com.robotutor.nexora.context.device.domain.event.DeviceCommissionedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceDomainEvent
import com.robotutor.nexora.context.device.domain.event.DeviceMetadataUpdatedEvent
import com.robotutor.nexora.context.device.domain.event.DeviceRegisteredEvent
import com.robotutor.nexora.context.device.domain.exception.DeviceError
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.device.domain.vo.SerialNo
import com.robotutor.nexora.shared.domain.AggregateRoot
import com.robotutor.nexora.shared.domain.exception.InvalidStateException
import com.robotutor.nexora.shared.domain.model.FeedId
import com.robotutor.nexora.shared.domain.vo.AccountId
import com.robotutor.nexora.shared.domain.vo.ActorId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.domain.vo.PremisesId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import java.time.Instant

class DeviceAggregate private constructor(
    val deviceId: DeviceId,
    val accountId: AccountId,
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
) : AggregateRoot<DeviceAggregate, DeviceId, DeviceDomainEvent>(deviceId) {

    fun getHealth(): DeviceHealth = health
    fun getState(): DeviceState = state
    fun getFeedIds(): Set<FeedId> = feedIds
    fun getUpdatedAt(): Instant = updatedAt
    fun getName(): Name = name
    fun getMetadata(): DeviceMetadata? = metadata

    fun commission(accountId: AccountId): DeviceAggregate {
        if (state != DeviceState.REGISTERED) {
            throw InvalidStateException(DeviceError.NEXORA0401)
        }
        this.state = DeviceState.COMMISSIONED
        this.updatedAt = Instant.now()
        addEvent(DeviceCommissionedEvent(deviceId, accountId, premisesId))
        return this
    }

    fun updateMetadata(metadata: DeviceMetadata): DeviceAggregate {
        if (state != DeviceState.REGISTERED) {
            throw InvalidStateException(DeviceError.NEXORA0402)
        }
        this.metadata = metadata
        this.updatedAt = Instant.now()
        addEvent(DeviceMetadataUpdatedEvent(deviceId, metadata))
        return this
    }

    fun activate(metaData: DeviceMetadata): DeviceAggregate {
        if (state != DeviceState.COMMISSIONED) {
            throw InvalidStateException(DeviceError.NEXORA0403)
        }
        this.state = DeviceState.ACTIVE
        this.metadata = metaData
        this.updatedAt = Instant.now()
        return this
    }

    companion object {
        fun register(
            accountId: AccountId,
            premisesId: PremisesId,
            name: Name,
            registeredBy: ActorId,
            zoneId: ZoneId,
        ): DeviceAggregate {
            val device = create(
                accountId = accountId,
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
            accountId: AccountId,
            feedIds: Set<FeedId> = emptySet(),
            state: DeviceState = DeviceState.REGISTERED,
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
                accountId = accountId,
                registeredBy = registeredBy,
                createdAt = createdAt,
                updatedAt = updatedAt,
            )
        }
    }

//    fun updateFeedIds(feedIds: FeedIds): Device {
//        this.feedIds = feedIds
//        return this
//    }
//
//    fun updateHealth(health: DeviceHealth): Device {
//        this.health = health
//        return this
//    }
}

enum class DeviceState {
    REGISTERED,
    COMMISSIONED,
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
