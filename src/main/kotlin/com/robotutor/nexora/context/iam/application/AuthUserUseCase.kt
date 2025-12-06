//package com.robotutor.nexora.context.iam.application
//
//import com.robotutor.nexora.common.security.createMono
//import com.robotutor.nexora.common.security.createMonoError
//import com.robotutor.nexora.context.iam.application.command.AuthenticateAccountCommand
//import com.robotutor.nexora.context.iam.application.view.TokenResponses
//import com.robotutor.nexora.context.iam.domain.entity.AuthUser
//import com.robotutor.nexora.context.iam.domain.entity.TokenPrincipalType
//import com.robotutor.nexora.context.iam.domain.repository.AuthUserRepository
//import com.robotutor.nexora.context.iam.domain.service.SecretService
//import com.robotutor.nexora.context.user.domain.exception.NexoraError
//import com.robotutor.nexora.shared.domain.exception.BadDataException
//import com.robotutor.nexora.shared.domain.model.UserContext
//import com.robotutor.nexora.shared.logger.Logger
//import com.robotutor.nexora.shared.logger.logOnError
//import com.robotutor.nexora.shared.logger.logOnSuccess
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//import reactor.kotlin.core.publisher.switchIfEmpty
//
//@Service
//class AuthUserUseCase(
//    private val authUserRepository: AuthUserRepository,
//    private val secretService: SecretService,
//    private val tokenUseCase: TokenUseCase,
//) {
//    private val logger = Logger(this::class.java)
//
//    fun login(authenticateAccountCommand: AuthenticateAccountCommand): Mono<TokenResponses> {
//        return validateCredentials(authenticateAccountCommand)
//            .flatMap { authUser ->
//                tokenUseCase.generateTokenWithRefreshToken(
//                    TokenPrincipalType.USER,
//                    UserContext(authUser.userId)
//                )
//            }
//    }
//
//    private fun validateCredentials(authenticateAccountCommand: AuthenticateAccountCommand): Mono<AuthUser> {
//        return authUserRepository.findByEmail(authenticateAccountCommand.email)
//            .switchIfEmpty { createMonoError(BadDataException(NexoraError.NEXORA0202)) }
//            .flatMap { authUser ->
////                val matches = secretService.matches(loginCommand.password, authUser.password)
////                if (!matches) {
////                    createMonoError(BadDataException(NexoraError.NEXORA0202))
////                } else {
//                createMono(authUser)
////                }
//            }
//            .logOnSuccess(logger, "Successfully validated credentials for ${authenticateAccountCommand.email}")
//            .logOnError(logger, "", "Failed to validate credentials for ${authenticateAccountCommand.email}")
//    }
//}