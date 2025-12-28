package com.robotutor.nexora.context.zone.application.usecase

import com.robotutor.nexora.context.device.domain.vo.ModelNo
import com.robotutor.nexora.context.zone.application.command.CreateWidgetsCommand
import com.robotutor.nexora.context.zone.application.policy.CreateWidgetsPolicy
import com.robotutor.nexora.context.zone.domain.aggregate.ZoneAggregate
import com.robotutor.nexora.context.zone.domain.entity.Widget
import com.robotutor.nexora.context.zone.domain.exception.ZoneError
import com.robotutor.nexora.context.zone.domain.repository.ZoneRepository
import com.robotutor.nexora.context.zone.domain.vo.ToggleWidgetMetadata
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import com.robotutor.nexora.shared.domain.vo.FeedId
import com.robotutor.nexora.shared.domain.vo.Name
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateWidgetsUseCase(
    private val zoneRepository: ZoneRepository,
    private val createWidgetsPolicy: CreateWidgetsPolicy,
    
) {
    private val logger = Logger(this::class.java)

    fun execute(command: CreateWidgetsCommand): Mono<ZoneAggregate> {
        return createWidgetsPolicy.evaluate(command)
            .errorOnDenied(ZoneError.NEXORA0202)
            .flatMap { createWidgets(command.modelNo, command.feedIds) }
            .flatMap { widgets ->
                zoneRepository.findByZoneIdAndPremisesId(command.zoneId, command.premisesId)
                    .map { zone -> zone.updateWidgets(widgets) }
            }
            .flatMap { zone -> zoneRepository.save(zone) }
            .logOnSuccess(logger, "Successfully created zone")
            .logOnError(logger, "Failed to create zone")
    }

    private fun createWidgets(modelNo: ModelNo, feedId: List<FeedId>): Mono<List<Widget>> {
        return createMono(
            listOf(
                Widget.register(Name("Light 1"), feedId[0], ToggleWidgetMetadata()),
                Widget.register(Name("Light 2"), feedId[1], ToggleWidgetMetadata()),
                Widget.register(Name("Light 3"), feedId[2], ToggleWidgetMetadata()),
                Widget.register(Name("Light 4"), feedId[3], ToggleWidgetMetadata()),
                Widget.register(Name("Light 5"), feedId[4], ToggleWidgetMetadata()),
                Widget.register(Name("Light 6"), feedId[5], ToggleWidgetMetadata()),
                Widget.register(Name("Light 7"), feedId[6], ToggleWidgetMetadata()),
                Widget.register(Name("Light 8"), feedId[7], ToggleWidgetMetadata()),
            )
        )
    }
}