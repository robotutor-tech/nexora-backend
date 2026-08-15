package com.robotutor.nexora.module.identity.application.service.account

import com.robotutor.nexora.module.identity.domain.policy.RotateCredentialPolicy
import com.robotutor.nexora.module.identity.domain.repository.AccountRepository
import com.robotutor.nexora.module.identity.domain.service.SecretEncoder
import com.robotutor.nexora.shared.application.logger.Logger
import org.springframework.stereotype.Service

@Service
class RotateCredentialService(
    private val rotateCredentialPolicy: RotateCredentialPolicy,
    private val accountRepository: AccountRepository,
    private val secretService: SecretEncoder,

    ) {
    private val logger = Logger(this::class.java)

//    fun execute(command: RotateCredentialCommand): Mono<Pair<CredentialId, RawSecret>> {
//        return accountRepository.findByPrincipalId(command.subjectId)
//            .enforcePolicy(rotateCredentialPolicy, IdentityError.NEXORA0208) { account ->
//                RotateCredentialPolicyContext(account, command.actorData)
//            }
////            .flatMap { account ->
////                val secret = CredentialSecret.generate()
////                account.rotateCredential(secretService.encode(secret), command.kind)
////                accountRepository.save(account)
////                    .map { Pair(account.getCredentials().first().credentialId, secret) }
////            }
//            .logOnSuccess(logger, "Successfully rotated credential", mapOf("accountId" to command.subjectId))
//            .logOnError(logger, "Failed to rotate credential", mapOf("accountId" to command.subjectId))
//    }
}
