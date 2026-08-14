package com.robotutor.nexora.shared.outbox.recorder

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class RecorderInfrastructureInitializer(val recorder: Recorder) {

    @PostConstruct
    fun init() {
        RecorderInfrastructure.recorder = recorder
    }
}
