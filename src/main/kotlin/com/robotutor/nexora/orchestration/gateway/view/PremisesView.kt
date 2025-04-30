package com.robotutor.nexora.orchestration.gateway.view

import java.time.LocalDateTime


data class PremisesView(
    val premisesId: String,
    val name: String,
    val createdAt: LocalDateTime,
    val actors: MutableList<PremisesActorView> = mutableListOf(),
) {
    fun addActors(actors: List<PremisesActorView>): PremisesView {
        this.actors.addAll(actors)
        this.actors.distinct()
        this.actors.sortBy { it.role.role }
        return this
    }
}
