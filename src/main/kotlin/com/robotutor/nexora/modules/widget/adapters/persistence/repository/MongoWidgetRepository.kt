package com.robotutor.nexora.modules.widget.adapters.persistence.repository

import com.robotutor.nexora.modules.widget.adapters.persistence.mapper.WidgetDocumentMapper
import com.robotutor.nexora.modules.widget.adapters.persistence.model.WidgetDocument
import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.modules.widget.domain.repository.WidgetRepository
import com.robotutor.nexora.shared.adapters.persistence.repository.MongoRepository
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.WidgetId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class MongoWidgetRepository(
    mongoTemplate: ReactiveMongoTemplate,
) : MongoRepository<Widget, WidgetDocument>(mongoTemplate, WidgetDocument::class.java, WidgetDocumentMapper()),
    WidgetRepository {
    override fun save(widget: Widget): Mono<Widget> {
        val query = Query(Criteria.where("widgetId").`is`(widget.widgetId.value))
        return this.findAndReplace(query, widget)
    }

    override fun findAllByPremisesIdAndWidgetIdIn(premisesId: PremisesId, widgetIds: List<WidgetId>): Flux<Widget> {
        val query = Query(
            Criteria.where("premisesId").`is`(premisesId.value)
                .and("widgetId").`in`(widgetIds.map { it.value })
        )
        return this.findAll(query)
    }
}

