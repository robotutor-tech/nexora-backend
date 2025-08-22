package com.robotutor.nexora.shared.logger

import com.robotutor.nexora.common.security.models.PremisesId
import reactor.util.context.Context
import reactor.util.context.ContextView

internal object ReactiveContext {

    private const val TRACE_ID_HEADER_KEY = "x-trace-id"
    private const val PREMISES_ID_HEADER_KEY = "x-premises-id"

    fun getTraceId(context: ContextView): String {
        return context.getOrDefault<String>(TRACE_ID_HEADER_KEY, null) ?: "missing-trace-id"
    }

    fun putTraceId(context: Context, traceId: String): Context {
        return context.put(TRACE_ID_HEADER_KEY, traceId)
    }

    fun putPremisesId(context: Context, premisesId: PremisesId): Context {
        return context.put(PREMISES_ID_HEADER_KEY, premisesId)
    }

    fun getPremisesId(context: ContextView): String {
        return context.getOrDefault<String>(PREMISES_ID_HEADER_KEY, null) ?: "missing-premises-id"
    }
}