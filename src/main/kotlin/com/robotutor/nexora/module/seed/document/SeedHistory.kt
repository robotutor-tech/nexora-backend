package com.robotutor.nexora.module.seed.document

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("seeds")
@TypeAlias("SeedHistory")
data class SeedHistory(
    @Id
    var id: String? = null,
    @Indexed(unique = true)
    val name: String,
    val timestamp: Instant = Instant.now(),
    @Version
    val version: Long? = null,
)
