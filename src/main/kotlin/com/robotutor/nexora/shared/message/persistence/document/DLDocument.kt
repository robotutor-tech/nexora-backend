package com.robotutor.nexora.shared.message.persistence.document

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@TypeAlias("DLDocument")
@Document("deadLetter")
data class DLDocument(
    @Id
    val id: String? = null,
    val topic: String,
    val message: String,
    val headers: List<KafkaHeader>,
    val status: Status = Status.DEAD_LETTER,
    val createdAt: Instant = Instant.now(),
    @Version
    val version: Long? = null,
) {
}

data class KafkaHeader(
    val key: String,
    val value: String,
)

enum class Status {
    DEAD_LETTER
}
