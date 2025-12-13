package com.robotutor.nexora.context.premises.infrastructure.persistence

import com.robotutor.nexora.context.premises.domain.repository.PremisesIdGenerator
import com.robotutor.nexora.context.premises.infrastructure.persistence.document.IdType
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.shared.domain.vo.PremisesId
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class MongoPremisesIdGenerator(private val idGeneratorService: IdGeneratorService) : PremisesIdGenerator {

    override fun generate(): Mono<PremisesId> {
        return idGeneratorService.generateId(IdType.PREMISE_ID)
            .map { PremisesId(it) }
    }
}