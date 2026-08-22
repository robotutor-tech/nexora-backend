package com.robotutor.nexora.module.master.infrastructure.persistence.document

import com.robotutor.nexora.module.master.domain.aggregate.Address
import com.robotutor.nexora.shared.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Address")
@Document("addresses")
data class AddressDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val pinCode: String,
    val city: String,
    val district: String,
    val state: String,
    val country: String,
    @Version
    val version: Long? = null,
) : MongoDocument<Address>
