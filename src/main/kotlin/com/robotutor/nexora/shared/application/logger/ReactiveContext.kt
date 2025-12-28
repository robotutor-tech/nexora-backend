package com.robotutor.nexora.shared.application.logger

import com.robotutor.nexora.shared.domain.vo.PremisesId
import reactor.util.context.Context
import reactor.util.context.ContextView
import java.time.Instant

internal object ReactiveContext {
    const val X_PREMISES_ID = "x-premises-id"
    const val CORRELATION_ID = "correlation-id"
    const val START_TIME = "start-time"

    fun putPremisesId(context: Context, premisesId: PremisesId): Context {
        return context.put(X_PREMISES_ID, premisesId.value)
    }

    fun getPremisesId(context: ContextView): PremisesId {
        val premisesId = context.getOrDefault<String>(X_PREMISES_ID, null) ?: "missing-premises-id"
        return PremisesId(premisesId)
    }

    fun putCorrelationId(context: Context, correlationId: String): Context {
        return context.put(CORRELATION_ID, correlationId)
    }

    fun getCorrelationId(context: ContextView): String {
        return context.getOrDefault<String>(CORRELATION_ID, null) ?: "missing-premises-id"
    }

    fun putStartTime(context: Context, startTime: Instant): Context {
        return context.put(START_TIME, startTime)
    }

    fun getStartTime(context: ContextView): Instant {
        return context.getOrDefault<Instant>(START_TIME, null) ?: Instant.now()
    }
}