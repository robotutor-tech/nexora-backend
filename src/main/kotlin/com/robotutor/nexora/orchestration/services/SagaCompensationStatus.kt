package com.robotutor.nexora.orchestration.services

import com.robotutor.nexora.kafka.models.KafkaTopicName
import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.saga.models.CompensateCommand
import com.robotutor.nexora.saga.models.Saga
import com.robotutor.nexora.saga.models.StepStatus
import com.robotutor.nexora.saga.services.SagaService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class SagaCompensationStatus(private val kafkaConsumer: KafkaConsumer, private val sagaService: SagaService) {


    @PostConstruct
    fun updateSagaStep(): Flux<Saga> {
        return kafkaConsumer.consume(
            listOf(
                "saga.compensate.user.delete.success",
                "saga.compensate.user.delete.failure",
                "saga.compensate.premises.delete.success",
                "saga.compensate.premises.delete.failure"
            ),
            CompensateCommand::class.java
        ) { it ->
            val stepNameWithStatus = getStepNameWithStatus(it.topic)
            sagaService.getSagaBySagaId(it.message.sagaId)
                .map { saga ->
                    when (stepNameWithStatus.second) {
                        StepStatus.COMPLETED -> saga.completeStep(stepNameWithStatus.first)
                        StepStatus.FAILED -> saga.failStep(stepNameWithStatus.first, it.message.error)
                        else -> saga
                    }
                }
                .map { saga -> saga.compensateSaga() }
                .flatMap { sagaService.storeSaga(it.saga) }
        }
    }


    private fun getStepNameWithStatus(topic: KafkaTopicName): Pair<String, StepStatus> {
        return when (topic) {
            "saga.compensate.user.delete.success" -> Pair("CompensateUser", StepStatus.COMPLETED)
            "saga.compensate.user.delete.failure" -> Pair("CompensateUser", StepStatus.FAILED)
            "saga.compensate.premises.delete.success" -> Pair("CompensatePremises", StepStatus.COMPLETED)
            "saga.compensate.premises.delete.failure" -> Pair("CompensatePremises", StepStatus.FAILED)
            else -> Pair("", StepStatus.FAILED)
        }
    }
}