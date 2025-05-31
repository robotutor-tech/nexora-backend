package com.robotutor.nexora.orchestration.gateway.view

import java.time.LocalDateTime

data class PremisesView(
    val premisesId: String,
    val name: String,
    val createdAt: LocalDateTime,
)

data class PremisesWithActorView(
    val premisesId: String,
    val name: String,
    val createdAt: LocalDateTime,
    val actor: PremisesActorView,
) {
    companion object {
        fun from(premises: PremisesView, actorView: PremisesActorView): PremisesWithActorView {
            return PremisesWithActorView(
                premisesId = premises.premisesId,
                name = premises.name,
                createdAt = premises.createdAt,
                actor = actorView
            )
        }
    }
}
