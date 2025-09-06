package com.robotutor.nexora.modules.automation.infrastructure.persistence.document

import org.apache.kafka.common.protocol.types.Field
import org.bson.types.ObjectId
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val AUTOMATION_EXECUTION_COLLECTION = "automationExecutions"

@TypeAlias("AutomationExecution")
@Document(AUTOMATION_EXECUTION_COLLECTION)
@CompoundIndexes(
    CompoundIndex(name = "execution_status_idx", def = "{'executionId':1, 'status':1}"),
    CompoundIndex(name = "resume_at_idx", def = "{'resumeAt':1, 'status':1}"),
)
data class AutomationExecution(
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val executionId: String,
    @Indexed
    val automationId: String,
    @Indexed
    val triggerId: Field.Str,
    val status: ExecutionStatus,
    val currentActionIndex: Int,
    val resumeAt: Instant,
    val lastError: String?,
    val startedAt: Instant,
    val completedAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long?
)

enum class ExecutionStatus {
    PENDING, RUNNING, WAITING, COMPLETED, FAILED, CANCELLED
}

