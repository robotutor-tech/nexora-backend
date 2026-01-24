package com.robotutor.nexora.module.automation.application.executor

import com.robotutor.nexora.module.automation.domain.vo.AutomationId
import java.time.Instant

/**
 * Result of executing an automation.
 *
 * This is an application layer result (not a domain entity) because execution typically
 * involves I/O and orchestration.
 */
data class AutomationExecutionResult(
    val automationId: AutomationId,
    val startedAt: Instant,
    val completedAt: Instant,
    val executedActionCount: Int,
)

