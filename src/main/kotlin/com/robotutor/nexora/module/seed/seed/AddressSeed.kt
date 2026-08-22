package com.robotutor.nexora.module.seed.seed

import com.robotutor.nexora.module.master.infrastructure.persistence.document.AddressDocument
import com.robotutor.nexora.module.master.infrastructure.persistence.repository.AddressDocumentRepository
import org.apache.commons.csv.CSVFormat
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Suppress("UNCHECKED_CAST")
@Component
class AddressSeed(
    override val name: String = "ADDRESS_01",
    private val addressDocumentRepository: AddressDocumentRepository
) : SeedData {
    override fun execute(): Mono<Any> {
        val reader = ClassPathResource("seed/addresses.csv").inputStream.bufferedReader(Charsets.UTF_8)
        val addresses = CSVFormat.DEFAULT.builder()
            .setHeader()
            .get()
            .parse(reader)
            .records
            .stream()
            .map { it.values() }
            .map { AddressDocument(pinCode = it[1], city = it[7], district = it[8], state = it[9], country = "India") }
            .collect(
                { HashMap<String, AddressDocument>() },
                { acc, item -> acc[item.pinCode] = item },
                { a, b -> a.putAll(b) }
            )

        return addressDocumentRepository.deleteAll()
            .thenMany(addressDocumentRepository.saveAll(addresses.values))
            .collectList() as Mono<Any>
    }
}
