package com.robotutor.nexora.modules.auth.infrastructure.persistence.document

import com.robotutor.nexora.modules.auth.domain.entity.AuthDevice
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


const val AUTH_DEVICE_COLLECTION = "authDevices"

@TypeAlias("AuthDevice")
@Document(AUTH_DEVICE_COLLECTION)
data class AuthDeviceDocument(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val deviceId: String,
    @Indexed(unique = true)
    val secret: String,
    val actorId: String,
    val roleId: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long?
) : MongoDocument<AuthDevice>