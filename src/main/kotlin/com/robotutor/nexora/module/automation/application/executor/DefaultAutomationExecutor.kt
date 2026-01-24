package com.robotutor.nexora.module.automation.application.executor

import com.robotutor.nexora.module.automation.domain.entity.ResolvedAutomation
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant

/**
 * Minimal implementation used to wire the pipeline end-to-end.
 *
 * It currently does not perform action side-effects. It only returns a result indicating
 * how many actions would be executed.
 *
 * Replace this with the real executor once the action execution semantics are defined.
 */
@Service
class DefaultAutomationExecutor : AutomationExecutor {
    override fun execute(automation: ResolvedAutomation): Mono<AutomationExecutionResult> {
        val startedAt = Instant.now()
        val completedAt = Instant.now()

        return Mono.just(
            AutomationExecutionResult(
                automationId = automation.automationId,
                startedAt = startedAt,
                completedAt = completedAt,
                executedActionCount = automation.actions.size,
            )
        )
    }
}

