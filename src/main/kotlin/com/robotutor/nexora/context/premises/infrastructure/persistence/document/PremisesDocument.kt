package com.robotutor.nexora.context.premises.infrastructure.persistence.document

import com.robotutor.nexora.context.premises.domain.aggregate.PremisesAggregate
import com.robotutor.nexora.context.premises.domain.aggregate.PremisesState
import com.robotutor.nexora.shared.domain.vo.AccountType
import com.robotutor.nexora.shared.infrastructure.persistence.document.MongoDocument
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

const val PREMISES_COLLECTION = "premises"

@TypeAlias("Premises")
@Document(PREMISES_COLLECTION)
data class PremisesDocument(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val premisesId: String,
    val name: String,
    val address: AddressDocument,
    val state: PremisesState,
    val registeredBy: RegisteredByDocument,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Version
    val version: Long? = null
) : MongoDocument<PremisesAggregate>

data class RegisteredByDocument(
    val accountId: String,
    val type: AccountType
)

data class AddressDocument(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val postalCode: String
)