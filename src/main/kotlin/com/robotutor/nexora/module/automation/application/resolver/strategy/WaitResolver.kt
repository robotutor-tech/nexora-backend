package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.domain.vo.component.Wait
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class WaitResolver : ComponentResolver<Wait, Wait> {
    override fun resolve(component: Wait): Mono<Wait> {
        return createMono(component)
    }
}
