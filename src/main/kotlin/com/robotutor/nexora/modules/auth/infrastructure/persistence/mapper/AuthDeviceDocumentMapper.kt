package com.robotutor.nexora.modules.auth.infrastructure.persistence.mapper

import com.robotutor.nexora.modules.auth.domain.entity.AuthDevice
import com.robotutor.nexora.modules.auth.domain.entity.DeviceSecret
import com.robotutor.nexora.modules.auth.infrastructure.persistence.document.AuthDeviceDocument
import com.robotutor.nexora.shared.domain.model.ActorId
import com.robotutor.nexora.shared.domain.model.DeviceId
import com.robotutor.nexora.shared.domain.model.RoleId
import com.robotutor.nexora.shared.infrastructure.persistence.mapper.DocumentMapper

object AuthDeviceDocumentMapper : DocumentMapper<AuthDevice, AuthDeviceDocument> {
    override fun toMongoDocument(domain: AuthDevice): AuthDeviceDocument {
        return AuthDeviceDocument(
            id = null,
            deviceId = domain.deviceId.value,
            secret = domain.secret.value,
            actorId = domain.actorId.value,
            roleId = domain.roleId.value,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            version = domain.version
        )
    }

    override fun toDomainModel(document: AuthDeviceDocument): AuthDevice {
        return AuthDevice(
            deviceId = DeviceId(document.deviceId),
            actorId = ActorId(document.actorId),
            roleId = RoleId(document.roleId),
            secret = DeviceSecret(document.secret),
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
            version = document.version,
        )
    }
}
