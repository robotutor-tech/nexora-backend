//package com.robotutor.nexora.context.iam.application
//
//import com.robotutor.nexora.context.iam.application.command.ActorLoginCommand
//import com.robotutor.nexora.context.iam.application.view.TokenResponses
//import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
//import com.robotutor.nexora.shared.domain.model.ActorContext
//import com.robotutor.nexora.shared.domain.model.UserContext
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//
//@Service
//class ActorLoginUseCase(private val tokenUseCase: TokenUseCase) {
//    private val logger = Logger(this::class.java)
//
//    fun actorLogin(actorLoginCommand: ActorLoginCommand): Mono<TokenResponses> {
//        return tokenUseCase.generateTokenWithRefreshToken(
//            principalType = TokenPrincipalType.ACTOR,
//            principalContext = ActorContext(
//                actorId = actorLoginCommand.actorId,
//                roleId = actorLoginCommand.roleId,
//                principalContext = UserContext(actorLoginCommand.userData.userId)
//            )
//        )
//            .flatMap { tokens ->
//                tokenUseCase.findTokenByValue(actorLoginCommand.token)
//                    .flatMap { token -> tokenUseCase.invalidateToken(token) }
//                    .map { tokens }
//            }
//            .logOnSuccess(logger, "Successfully logged in actor")
//            .logOnError(logger, "", "Failed to log in actor")
//    }
//}