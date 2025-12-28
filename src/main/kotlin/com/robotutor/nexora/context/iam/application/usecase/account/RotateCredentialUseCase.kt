package com.robotutor.nexora.context.iam.application.usecase.account

import com.robotutor.nexora.context.iam.application.command.RotateCredentialCommand
import com.robotutor.nexora.context.iam.application.policy.RotateCredentialPolicy
import com.robotutor.nexora.context.iam.domain.exception.IAMError
import com.robotutor.nexora.context.iam.domain.repository.AccountRepository
import com.robotutor.nexora.context.iam.domain.service.SecretEncoder
import com.robotutor.nexora.context.iam.domain.vo.CredentialId
import com.robotutor.nexora.context.iam.domain.vo.CredentialSecret
import com.robotutor.nexora.shared.domain.utility.errorOnDenied
import com.robotutor.nexora.shared.application.logger.Logger
import com.robotutor.nexora.shared.application.logger.logOnError
import com.robotutor.nexora.shared.application.logger.logOnSuccess
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RotateCredentialUseCase(
    private val rotateCredentialPolicy: RotateCredentialPolicy,
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,

    ) {
    private val logger = Logger(this::class.java)

    fun execute(command: RotateCredentialCommand): Mono<Pair<CredentialId, CredentialSecret>> {
        return rotateCredentialPolicy.evaluate(command)
            .errorOnDenied(IAMError.NEXORA0208)
            .flatMap { accountRepository.findByPrincipalId(command.principalId) }
            .flatMap { account ->
                val secret = CredentialSecret.generate()
                account.rotateCredential(secretService.encode(secret), command.kind)
                accountRepository.save(account)
                    .map { Pair(account.getCredentials().first().credentialId, secret) }
            }
            .logOnSuccess(logger, "Successfully rotated credential", mapOf("accountId" to command.principalId))
            .logOnError(logger, "Failed to rotate credential", mapOf("accountId" to command.principalId))
    }
}