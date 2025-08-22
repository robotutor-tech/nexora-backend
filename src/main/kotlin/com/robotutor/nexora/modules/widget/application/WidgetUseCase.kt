package com.robotutor.nexora.modules.widget.application

import com.robotutor.nexora.modules.widget.domain.model.Widget
import com.robotutor.nexora.modules.widget.domain.repository.WidgetRepository
import com.robotutor.nexora.shared.domain.model.ActorData
import com.robotutor.nexora.shared.domain.model.WidgetId
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class WidgetUseCase(private val widgetRepository: WidgetRepository) {

    fun getWidgets(actorData: ActorData, widgetIds: List<WidgetId>): Flux<Widget> {
        return widgetRepository.findAllByPremisesIdAndWidgetIdIn(actorData.premisesId, widgetIds)
    }

}