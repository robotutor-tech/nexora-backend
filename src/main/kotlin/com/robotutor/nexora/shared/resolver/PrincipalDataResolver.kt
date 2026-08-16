package com.robotutor.nexora.shared.resolver

import com.robotutor.nexora.shared.context.ReactiveContext
import com.robotutor.nexora.shared.domain.exception.DataNotFoundException
import com.robotutor.nexora.shared.domain.exception.SharedNexoraError
import com.robotutor.nexora.shared.domain.vo.AccountData
import com.robotutor.nexora.shared.domain.vo.ActorData
import com.robotutor.nexora.shared.domain.vo.DeviceData
import com.robotutor.nexora.shared.domain.vo.PrincipalData
import com.robotutor.nexora.shared.domain.vo.UserData
import com.robotutor.nexora.shared.utility.createMono
import com.robotutor.nexora.shared.utility.createMonoError
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.lang.reflect.Parameter
import kotlin.jvm.java

@Component
class PrincipalDataResolver : ArgumentResolver {
    override fun supportsParameter(parameter: Parameter): Boolean {
        return parameter.type == PrincipalData::class.java
    }

    override fun resolveArgument(parameter: Parameter): Mono<Any> {
        return ReactiveContext.getPrincipalData()
            .map { it }
    }
}
