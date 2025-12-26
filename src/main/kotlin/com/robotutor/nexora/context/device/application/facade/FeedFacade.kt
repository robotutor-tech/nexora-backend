package com.robotutor.nexora.context.device.application.facade

import com.robotutor.nexora.context.device.domain.vo.DeviceId
import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.shared.domain.vo.FeedId
import reactor.core.publisher.Flux

interface FeedFacade {
    fun registerFeeds(deviceId: DeviceId, modelNo: ModelNo): Flux<FeedId>
}
