package com.robotutor.nexora.context.iam.interfaces.controller

import com.robotutor.nexora.context.iam.application.command.RefreshSessionCommand
import com.robotutor.nexora.context.iam.application.usecase.RefreshSessionUseCase
import com.robotutor.nexora.context.iam.application.usecase.ValidateSessionUseCase
import com.robotutor.nexora.context.iam.domain.vo.TokenValue
import com.robotutor.nexora.context.iam.interfaces.controller.mapper.SessionMapper
import com.robotutor.nexora.context.iam.interfaces.controller.view.SessionValidateResponse
import com.robotutor.nexora.context.iam.interfaces.controller.view.TokenResponses
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/iam/sessions")
class SessionController(
    private val validateSessionUseCase: ValidateSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase,
) {

    @GetMapping("/validate")
    fun validate(@RequestHeader("authorization") token: String = ""): Mono<SessionValidateResponse> {
        val command = SessionMapper.toValidateSessionCommand(token)
        return validateSessionUseCase.execute(command)
            .map { SessionMapper.toValidateSessionResponse(it) }

    }

    @GetMapping("/refresh")
    fun refresh(@RequestHeader("authorization") token: String = ""): Mono<TokenResponses> {
        val command = RefreshSessionCommand(TokenValue(token.removePrefix("Bearer ")))
        return refreshSessionUseCase.execute(command)
            .map { SessionMapper.toTokenResponses(it) }
    }
}
