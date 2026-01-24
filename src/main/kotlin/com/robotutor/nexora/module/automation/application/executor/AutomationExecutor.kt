package com.robotutor.nexora.module.automation.application.executor

import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import reactor.core.publisher.Mono

/**
 * Executes a fully resolved automation.
 *
 * This is an application-layer contract because executing actions typically triggers I/O (facades, messaging, etc.).
 */
fun interface AutomationExecutor {
    fun execute(automation: ResolvedAutomation): Mono<AutomationExecutionResult>
}

