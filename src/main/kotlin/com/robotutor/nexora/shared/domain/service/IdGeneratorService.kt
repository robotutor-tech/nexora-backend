package com.robotutor.nexora.shared.domain.service

import com.robotutor.nexora.shared.domain.model.IdSequenceType
import reactor.core.publisher.Mono

interface IdGeneratorService {
    fun generateId(idType: IdSequenceType): Mono<String>
}