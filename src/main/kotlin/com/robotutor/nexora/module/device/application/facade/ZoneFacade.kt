package com.robotutor.nexora.module.device.application.facade

import com.robotutor.nexora.module.device.domain.vo.ModelNo
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.ZoneId
import reactor.core.publisher.Mono

interface ZoneFacade {
    fun registerWidgets(zoneId: ZoneId, modelNo: ModelNo, feedIds: List<FeedId>): Mono<Any>
}