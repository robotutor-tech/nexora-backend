package com.robotutor.nexora.shared.domain.service

import com.robotutor.nexora.shared.persistence.document.IdSequenceType
import reactor.core.publisher.Mono

interface IdGeneratorService {
    fun generate(idType: IdSequenceType): Mono<String>
}
