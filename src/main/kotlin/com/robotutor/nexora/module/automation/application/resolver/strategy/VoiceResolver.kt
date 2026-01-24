package com.robotutor.nexora.module.automation.application.resolver.strategy

import com.robotutor.nexora.module.automation.domain.vo.component.Voice
import com.robotutor.nexora.shared.utility.createMono
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class VoiceResolver : ComponentResolver<Voice, Voice> {

    override fun resolve(component: Voice): Mono<Voice> {
        return createMono(component)
    }
}
