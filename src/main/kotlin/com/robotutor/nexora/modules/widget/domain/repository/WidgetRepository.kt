package com.robotutor.nexora.modules.widget.domain.repository

import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.WidgetId
import reactor.core.publisher.Flux

interface WidgetRepository {
    fun findAllByPremisesIdAndWidgetIdIn(premisesId: PremisesId, widgetIds: List<WidgetId>): Flux<Widget>
}