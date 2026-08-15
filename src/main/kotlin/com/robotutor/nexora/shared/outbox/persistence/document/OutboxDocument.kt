package com.robotutor.nexora.shared.outbox.persistence.document

import com.robotutor.nexora.shared.message.message.EventMessage
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("OutboxDocument")
@Document("outbox")
data class OutboxDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val eventId: String,
    val correlationId: String,
    val message: EventMessage,
    val occurredAt: Instant,
    val principalData: PrincipalDataDocument?,
    var status: Status = Status.PENDING,
    val createdAt: Instant = Instant.now(),
    @Version
    val version: Long? = null,
){
    fun markAsPublished(): OutboxDocument {
        this.status = Status.PUBLISHED
        return this
    }

    fun markAsDL(): OutboxDocument {
        this.status = Status.DEAD_LETTER
        return this
    }
}

enum class Status {
    PENDING,
    PUBLISHED,
    DEAD_LETTER
}
