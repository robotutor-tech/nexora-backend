package com.robotutor.nexora.modules.widget.domain.repository

import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetId
import com.robotutor.nexora.shared.domain.model.PremisesId
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface WidgetRepository {
    fun findAllByPremisesIdAndWidgetIdIn(premisesId: PremisesId, widgetIds: List<WidgetId>): Flux<Widget>
    fun save(widget: Widget): Mono<Widget>
}