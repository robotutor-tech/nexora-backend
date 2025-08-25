package com.robotutor.nexora.shared.domain.service

import com.robotutor.nexora.shared.domain.model.IdSequenceType
import com.robotutor.nexora.shared.domain.model.SequenceId
import reactor.core.publisher.Mono

interface IdGeneratorService {
    fun <T : SequenceId> generateId(idType: IdSequenceType, clazz: Class<T>): Mono<T>
}