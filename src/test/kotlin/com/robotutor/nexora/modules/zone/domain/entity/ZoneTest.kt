//package com.robotutor.nexora.context.zone.domain.entity
//
//import com.robotutor.nexora.context.zone.domain.event.ZoneCreatedEvent
//import com.robotutor.nexora.shared.domain.vo.ActorId
//import com.robotutor.nexora.shared.domain.vo.Name
//import com.robotutor.nexora.shared.domain.vo.PremisesId
//import com.robotutor.nexora.shared.domain.model.ZoneId
//import io.kotest.matchers.shouldBe
//import org.junit.jupiter.api.Test
//import java.time.Instant
//
//class ZoneTest {
//    @Test
//    fun `create should initialize fields and emit ZoneCreatedEvent`() {
//        val zone = Zone.create(
//            zoneId = ZoneId("zone-0001"),
//            premisesId = PremisesId("prem-1"),
//            name = Name("Living"),
//            createdBy = ActorId("actor-1")
//        )
//
//        zone.zoneId.value shouldBe "zone-0001"
//        zone.premisesId.value shouldBe "prem-1"
//        zone.name.value shouldBe "Living"
//        zone.createdBy.value shouldBe "actor-1"
//        // createdAt is set at creation time; verifying domain event instead of timestamp equality
//        val events = zone.getDomainEvents()
//        events.size shouldBe 1
//        val event = events[0] as ZoneCreatedEvent
//        event.zoneId shouldBe ZoneId("zone-0001")
//        event.name shouldBe Name("Living")
//    }
//}
//
