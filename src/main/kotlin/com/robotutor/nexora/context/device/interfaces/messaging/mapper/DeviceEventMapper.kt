package com.robotutor.nexora.context.device.interfaces.messaging.mapper

import com.robotutor.nexora.context.device.application.command.CommissionDeviceCommand
import com.robotutor.nexora.context.device.application.command.CompensateDeviceRegistrationCommand
import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.interfaces.messaging.message.CompensateDeviceRegistrationMessage
import com.robotutor.nexora.context.device.interfaces.messaging.message.DeviceAccountCreatedMessage
import com.robotutor.nexora.shared.domain.vo.AccountId

object DeviceEventMapper {
//    fun toDeviceId(message: DeviceFeedsCreatedMessage): DeviceId {
//        return DeviceId(value = message.deviceId)
//    }
//
//    fun toFeedIds(message: DeviceFeedsCreatedMessage): FeedIds {
//        val feedIds = message.feedIds.map { feed -> FeedId(feed) }
//        return FeedIds(feedIds)
//    }

    fun toCompensateDeviceRegistrationCommand(eventMessage: CompensateDeviceRegistrationMessage): CompensateDeviceRegistrationCommand {
        return CompensateDeviceRegistrationCommand(DeviceId(eventMessage.deviceId))
    }

    fun toCommissionDeviceCommand(eventMessage: DeviceAccountCreatedMessage): CommissionDeviceCommand {
        return CommissionDeviceCommand(
            accountId = AccountId(eventMessage.accountId),
            deviceId = DeviceId(eventMessage.credentialId)
        )
    }
}