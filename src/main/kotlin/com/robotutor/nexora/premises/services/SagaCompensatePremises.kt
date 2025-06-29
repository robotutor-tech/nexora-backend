package com.robotutor.nexora.premises.services

import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.saga.models.CompensateCommand
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class SagaCompensatePremises(
    private val kafkaConsumer: KafkaConsumer,
    private val kafkaPublisher: KafkaPublisher,
    private val premisesService: PremisesService
) {

    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("saga.compensate.premises.delete"), CompensateCommand::class.java) {
            val premisesCommand = it.message
            premisesService.deletePremises(premisesCommand.resourceId)
                .flatMap {
                    kafkaPublisher.publish("saga.compensate.premises.delete.success", premisesCommand)
                }
                .onErrorResume { throwable ->
                    premisesCommand.error = throwable.message
                    kafkaPublisher.publish("saga.compensate.premises.delete.failure", premisesCommand)
                }
        }
            .subscribe()
    }
}
