package com.robotutor.nexora.modules.orchestration.gateway.view

import java.time.Instant

data class PremisesView(
    val premisesId: String,
    val name: String,
    val createdAt: Instant,
)

data class PremisesWithActorView(
    val premisesId: String,
    val name: String,
    val createdAt: Instant,
    val actor: PremisesActorView,
) {
//    companion object {
//        fun from(premises: PremisesView, actorView: PremisesActorView): PremisesWithActorView {
//            return PremisesWithActorView(
//                premisesId = premises.premisesId,
//                name = premises.name,
//                createdAt = premises.createdAt,
//                actor = actorView
//            )
//        }
//    }
}
