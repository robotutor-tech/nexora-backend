//package com.robotutor.nexora.modules.iam.infrastructure.persistence.repository
//
//import com.robotutor.nexora.modules.iam.domain.entity.Role
//import com.robotutor.nexora.modules.iam.domain.repository.RoleRepository
//import com.robotutor.nexora.modules.iam.infrastructure.persistence.mapper.RoleDocumentMapper
//import com.robotutor.nexora.modules.iam.infrastructure.persistence.document.RoleDocument
//import com.robotutor.nexora.shared.domain.model.PremisesId
//import com.robotutor.nexora.shared.domain.model.RoleId
//import com.robotutor.nexora.shared.domain.model.RoleType
//import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
//import org.springframework.data.mongodb.core.ReactiveMongoTemplate
//import org.springframework.data.mongodb.core.query.Criteria
//import org.springframework.data.mongodb.core.query.Query
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Flux
//import reactor.core.publisher.Mono
//
//@Service
//class MongoRoleRepository(
//    mongoTemplate: ReactiveMongoTemplate,
//) : MongoRepository<Role, RoleDocument>(mongoTemplate, RoleDocument::class.java, RoleDocumentMapper),
//    RoleRepository {
//    override fun save(role: Role): Mono<Role> {
//        val query = Query(Criteria.where("roleId").`is`(role.roleId.value))
//        return this.findAndReplace(query, role)
//    }
//
//    override fun findAllByPremisesIdAndRoleIdIn(premisesId: PremisesId, roleIds: List<RoleId>): Flux<Role> {
//        val query = Query(
//            Criteria.where("premisesId").`is`(premisesId.value)
//                .and("roleId").`in`(roleIds.map { it.value })
//        )
//        return this.findAll(query)
//    }
//
//    override fun findByRoleId(roleId: RoleId): Mono<Role> {
//        val query = Query(Criteria.where("roleId").`is`(roleId.value))
//        return this.findOne(query)
//    }
//
//    override fun findAllByPremisesIdAndRoleTypeIn(premisesId: PremisesId, roleTypes: List<RoleType>): Flux<Role> {
//        val query = Query(
//            Criteria.where("premisesId").`is`(premisesId.value)
//                .and("roleType").`in`(roleTypes)
//        )
//        return this.findAll(query)
//    }
//}
