package com.robotutor.nexora.modules.widget.repositories

import com.robotutor.nexora.modules.premises.models.PremisesId
import com.robotutor.nexora.modules.widget.models.Widget
import com.robotutor.nexora.modules.widget.models.WidgetId
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface WidgetRepository : ReactiveCrudRepository<Widget, WidgetId> {
    fun findAllByPremisesIdAndWidgetIdIn(premisesId: PremisesId, widgetIds: List<WidgetId>): Flux<Widget>
}
