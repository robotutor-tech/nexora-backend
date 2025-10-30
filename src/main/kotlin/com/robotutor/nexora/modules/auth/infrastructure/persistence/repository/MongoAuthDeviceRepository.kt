package com.robotutor.nexora.modules.auth.infrastructure.persistence.repository

import com.robotutor.nexora.modules.auth.domain.entity.AuthDevice
import com.robotutor.nexora.modules.auth.domain.entity.AuthUser
import com.robotutor.nexora.modules.auth.domain.entity.DeviceSecret
import com.robotutor.nexora.modules.auth.domain.repository.AuthDeviceRepository
import com.robotutor.nexora.modules.auth.domain.repository.AuthUserRepository
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.AuthDeviceDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper.AuthUserDocumentMapper
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.AuthUserDocument
import com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper.AuthDeviceDocumentMapper
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.Email
import com.robotutor.nexora.shared.infrastructure.persistence.repository.MongoRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAuthDeviceRepository(
    mongoTemplate: ReactiveMongoTemplate
) : MongoRepository<AuthDevice, AuthDeviceDocument>(
    mongoTemplate, AuthDeviceDocument::class.java,
    AuthDeviceDocumentMapper
), AuthDeviceRepository {
    override fun save(authDevice: AuthDevice): Mono<AuthDevice> {
        val query = Query(Criteria.where("deviceId").`is`(authDevice.deviceId.value))
        return this.findAndReplace(query, authDevice)
    }

    override fun findByDeviceIdAndDeviceSecret(deviceId: DeviceId, deviceSecret: DeviceSecret): Mono<AuthDevice> {
        val query = Query(
            Criteria.where("deviceId").`is`(deviceId.value)
                .and("secret").`is`(deviceSecret.value)
        )
        return this.findOne(query)
    }
}