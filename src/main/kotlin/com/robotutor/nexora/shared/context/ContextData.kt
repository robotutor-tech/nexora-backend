package com.robotutor.nexora.shared.context

import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import reactor.core.publisher.Mono
import reactor.util.context.ContextView

data class ContextData(
    val correlationId: String,
    val principalData: PrincipalData? = null,
)
