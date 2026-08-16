package com.robotutor.nexora.shared.resolver

import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.exception.UnAuthorizedException
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter

@Component
class DeviceDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == DeviceData::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return ReactiveContext.getPrincipalData()
            .flatMap { principalData ->
                when (principalData) {
                    is AccountData -> resolveAccountData(principalData)
                    is ActorData -> resolveAccountData(principalData.accountData)
                }
            }
    }

    private fun resolveAccountData(accountData: AccountData): Mono<DeviceData> {
        return when (accountData) {
            is UserData -> createMonoError(UnAuthorizedException(SharedNexoraError.NEXORA0107))
            is DeviceData -> createMono(accountData)
        }
    }
}
