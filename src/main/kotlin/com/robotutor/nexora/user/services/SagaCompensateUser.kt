package com.robotutor.nexora.user.services

import com.robotutor.nexora.kafka.services.KafkaConsumer
import com.robotutor.nexora.kafka.services.KafkaPublisher
import com.robotutor.nexora.saga.models.CompensateCommand
import com.robotutor.nexora.security.models.UserId
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class SagaCompensateUser(
    private val kafkaConsumer: KafkaConsumer,
    private val userService: UserService,
    private val kafkaPublisher: KafkaPublisher
) {
    @PostConstruct
    fun init() {
        kafkaConsumer.consume(listOf("saga.compensate.user.delete"), CompensateCommand::class.java) {
            val userCommand = it.message
            userService.deleteUserByUserId(userCommand.resourceId)
                .flatMap {
                    kafkaPublisher.publish("saga.compensate.user.delete.success", userCommand)
                }
                .onErrorResume { throwable ->
                    userCommand.error = throwable.message
                    kafkaPublisher.publish("saga.compensate.user.delete.failure", userCommand)
                }
        }
            .subscribe()
    }
}

