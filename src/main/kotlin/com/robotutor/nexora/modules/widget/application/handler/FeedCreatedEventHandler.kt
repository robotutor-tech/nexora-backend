//package com.robotutor.nexora.modules.widget.application.handler
//
//import com.robotutor.nexora.modules.widget.application.WidgetUseCase
//import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
//import com.robotutor.nexora.shared.domain.event.EventHandler
//import com.robotutor.nexora.shared.domain.event.FeedCreatedEvent
//import com.robotutor.nexora.shared.domain.model.ActorData
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class FeedCreatedEventHandler(private val widgetUseCase: WidgetUseCase) :
//    EventHandler<FeedCreatedEvent> {
//
//    fun handle(event: FeedCreatedEvent, actorData: ActorData): Mono<Any> {
//        val createWidgetCommand = CreateWidgetCommand(
//            name = event.name,
//            feedId = event.feedId,
//            zoneId = event.zoneId,
//            widgetType = event.widgetType
//        )
//        return widgetUseCase.createWidget(createWidgetCommand, actorData)
//            .map { event }
//    }
//}