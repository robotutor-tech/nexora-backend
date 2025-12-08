package com.robotutor.nexora.context.premises.infrastructure.persistence.repository

import com.robotutor.nexora.context.premises.domain.repository.PremisesIdGenerator
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.IdType
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoPremisesIdGenerator(private val idGeneratorService: IdGeneratorService) : PremisesIdGenerator {

    override fun generate(): Mono<PremisesId> {
        return idGeneratorService.generateId(IdType.PREMISE_ID, PremisesId::class.java)
    }
}