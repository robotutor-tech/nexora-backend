package com.robotutor.nexora.automation.models

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
    val executionId: ExecutionId,
    @Indexed
    val automationId: AutomationId,
    @Indexed
    val triggerId: TriggerId,
    val status: ExecutionStatus = ExecutionStatus.PENDING,
    val currentActionIndex: Int = 0,
    val resumeAt: Instant = Instant.now(),
    val lastError: String? = null,
    val startedAt: Instant = Instant.now(),
    val completedAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    @Version
    val version: Long? = null
)

enum class ExecutionStatus {
    PENDING, RUNNING, WAITING, COMPLETED, FAILED, CANCELLED
}

typealias ExecutionId = String
