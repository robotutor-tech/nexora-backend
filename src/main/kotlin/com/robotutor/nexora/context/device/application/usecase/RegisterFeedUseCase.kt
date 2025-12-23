package com.robotutor.nexora.context.device.application.usecase

import com.robotutor.nexora.context.device.application.command.RegisterFeedCommand
import com.robotutor.nexora.context.device.domain.aggregate.FeedAggregate
import com.robotutor.nexora.context.device.domain.repository.FeedRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class RegisterFeedUseCase(
    private val feedRepository: FeedRepository,
) {
    fun execute(commands: List<RegisterFeedCommand>): Flux<FeedAggregate> {
        val feeds = commands.map {
            FeedAggregate.register(
                deviceId = it.deviceId,
                type = it.type,
                range = it.range,
                premisesId = it.premisesId,
            )
        }
        return feedRepository.saveAll(feeds)
    }
}
