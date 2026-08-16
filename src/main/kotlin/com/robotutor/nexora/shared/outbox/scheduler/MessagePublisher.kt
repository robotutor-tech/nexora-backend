package com.robotutor.nexora.shared.outbox.scheduler

import com.robotutor.nexora.shared.message.services.KafkaEventPublisher
import com.robotutor.nexora.shared.outbox.persistence.document.Status
import com.robotutor.nexora.shared.outbox.persistence.repository.OutboxDocumentRepository
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
@EnableScheduling
class MessagePublisher(
    private val kafkaEventPublisher: KafkaEventPublisher,
    private val outboxDocumentRepository: OutboxDocumentRepository,
) {

    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "messagePublisherJob", lockAtMostFor = "PT4M", lockAtLeastFor = "PT1M")
    fun start() {
        println("================MESSAGE PUBLISHER SCHEDULER STARTED=================")
        outboxDocumentRepository.findAllByStatus(Status.PENDING)
            .flatMap { document ->
                kafkaEventPublisher.publish(document.message, document.context)
                    .flatMap { outboxDocumentRepository.save(document.markAsPublished()) }
                    .onErrorResume { outboxDocumentRepository.save(document.markAsPublished()) }
            }
            .doOnComplete {
                println("================MESSAGE PUBLISHER SCHEDULER STOPPED=================")
            }
            .subscribe()
    }
}
