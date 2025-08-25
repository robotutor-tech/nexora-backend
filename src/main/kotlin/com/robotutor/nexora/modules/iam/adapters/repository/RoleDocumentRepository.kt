package com.robotutor.nexora.modules.iam.adapters.repository

import com.robotutor.nexora.modules.iam.adapters.model.RoleDocument
import com.robotutor.nexora.shared.domain.model.PremisesId
import com.robotutor.nexora.shared.domain.model.RoleType
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface RoleDocumentRepository : ReactiveMongoRepository<RoleDocument, String> {
    fun findByRoleId(roleId: String): Mono<RoleDocument>
    fun findAllByPremisesIdAndRoleIdIn(premisesId: String, roles: List<String>): Flux<RoleDocument>
    fun findAllByPremisesIdAndRoleTypeIn(premisesId: String, roleTypes: List<RoleType>): Flux<RoleDocument>
}