package com.robotutor.nexora.context.device.application.facade

import com.robotutor.nexora.context.device.domain.aggregate.DeviceAggregate
import com.robotutor.nexora.shared.domain.vo.FeedId
import reactor.core.publisher.Mono

interface ZoneFacade {
    fun registerWidgets(device: DeviceAggregate, feedIds: List<FeedId>): Mono<Any>
}