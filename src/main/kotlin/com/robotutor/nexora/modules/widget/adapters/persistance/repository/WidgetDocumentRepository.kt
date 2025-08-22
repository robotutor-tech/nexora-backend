package com.robotutor.nexora.modules.widget.adapters.persistance.repository

import com.robotutor.nexora.modules.widget.adapters.persistance.model.WidgetDocument
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface WidgetDocumentRepository : ReactiveCrudRepository<WidgetDocument, String>{
    fun findAllByPremisesIdAndWidgetIdIn(premisesId: String, widgetIds: List<String>): Flux<WidgetDocument>
}