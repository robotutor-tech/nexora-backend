package com.robotutor.nexora.modules.widget.adapters.persistence.repository

import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.modules.widget.domain.repository.WidgetRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.WidgetId
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoWidgetRepository(private val widgetDocumentRepository: WidgetDocumentRepository) : WidgetRepository {
    override fun findAllByPremisesIdAndWidgetIdIn(premisesId: PremisesId, widgetIds: List<WidgetId>): Flux<Widget> {
        return widgetDocumentRepository.findAllByPremisesIdAndWidgetIdIn(premisesId.value, widgetIds.map { it.value })
            .map { it.toDomainModel() }
    }

    override fun save(widget: Widget): Mono<Widget> {
        TODO("Not yet implemented")
    }
}