package com.robotutor.nexora.context.feed.interfaces.controller.mapper

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.feed.application.command.GetFeedsQuery
import com.robotutor.nexora.context.feed.application.command.RegisterFeedsCommand
import com.robotutor.nexora.context.feed.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.feed.domain.vo.FeedValueRange
import com.robotutor.nexora.context.feed.interfaces.controller.view.FeedResponse
import com.robotutor.nexora.context.feed.interfaces.controller.view.FeedValueRangeResponse
import com.robotutor.nexora.context.feed.interfaces.controller.view.RegisterFeedsRequest
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.interfaces.view.AuthorizedResources

object FeedMapper {
    fun toGetFeedsQuery(resources: AuthorizedResources, actorData: ActorData): GetFeedsQuery {
        return GetFeedsQuery(actorData.actorId, resources.toResources(FeedId::class.java))
    }

    fun toFeedResponse(feed: FeedAggregate): FeedResponse {
        return FeedResponse(
            feedId = feed.feedId.value,
            deviceId = feed.deviceId.value,
            premisesId = feed.premisesId.value,
            type = feed.type,
            value = feed.getValue(),
            range = toFeedValueRangeResponse(feed.range),
            createdAt = feed.createdAt,
            updatedAt = feed.getUpdatedAt()
        )
    }

    private fun toFeedValueRangeResponse(range: FeedValueRange): FeedValueRangeResponse {
        return FeedValueRangeResponse(mode = range.mode, min = range.min, max = range.max)
    }

    fun toRegisterFeedsCommand(request: RegisterFeedsRequest, actorData: ActorData): RegisterFeedsCommand {
        return RegisterFeedsCommand(
            premisesId = actorData.premisesId,
            deviceId = DeviceId(request.deviceId),
            modelNo = ModelNo(request.modelNo)
        )
    }
}

