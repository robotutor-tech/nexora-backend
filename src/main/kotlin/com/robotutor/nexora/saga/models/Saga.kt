package com.robotutor.nexora.saga.models

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val SAGA_COLLECTION = "sagas"

@TypeAlias("Saga")
@Document(SAGA_COLLECTION)
data class Saga(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val sagaId: String,
    val name: String,
    val createdAt: Instant = Instant.now(),
    var updatedAt: Instant = Instant.now(),
    var status: SagaStatus,
    val metadata: Map<String, Any?>,
    val steps: MutableList<SagaStep> = mutableListOf(),
    @Indexed
    val traceId: String,
    @Version
    val version: Long? = null
) {

}

enum class SagaStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    COMPENSATED,
    FAILED
}


data class SagaStep(
    val name: String,
    var status: StepStatus,
    val resource: Map<String, Any?>?,
    val startedAt: Instant = Instant.now(),
    var endedAt: Instant? = null,
    var error: String? = null,
) {
}

enum class StepStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED,
}