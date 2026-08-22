package com.robotutor.nexora.module.master.infrastructure.persistence

import com.robotutor.nexora.module.master.domain.aggregate.Address
import com.robotutor.nexora.module.master.domain.repository.AddressRepository
import com.robotutor.nexora.module.master.domain.vo.PinCode
import com.robotutor.nexora.module.master.infrastructure.persistence.document.AddressDocument
import com.robotutor.nexora.module.master.infrastructure.persistence.mapper.AddressDocumentMapper
import com.robotutor.nexora.module.master.infrastructure.persistence.repository.AddressDocumentRepository
import com.robotutor.nexora.shared.cache.annotation.CacheEvicts
import com.robotutor.nexora.shared.cache.service.CacheService
import com.robotutor.nexora.shared.persistence.repository.retryOptimisticLockingFailure
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MongoAddressRepository(
    private val addressDocumentRepository: AddressDocumentRepository,
    private val cacheService: CacheService
) : AddressRepository {

    @CacheEvicts(["address::pincode:#{addressAggregate.pinCode.value}"])
    override fun save(address: Address): Mono<Address> {
        val document = AddressDocumentMapper.toDocument(address)
        return addressDocumentRepository.save(document)
            .retryOptimisticLockingFailure()
            .map { AddressDocumentMapper.toDomain(it) }
    }

    override fun find(pinCode: PinCode): Mono<Address> {
        return cacheService.retrieve("address::pincode:${pinCode.value}", AddressDocument::class.java) {
            addressDocumentRepository.findByPinCode(pinCode.value)
        }
            .map { AddressDocumentMapper.toDomain(it) }
    }
}
