package com.robotutor.nexora.modules.zone.interfaces.controller

import com.robotutor.nexora.modules.zone.application.ZoneUseCase
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneRequest
import com.robotutor.nexora.modules.zone.interfaces.controller.dto.ZoneResponse
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.modules.zone.domain.entity.Zone
import com.robotutor.nexora.testUtils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant

class ZoneControllerTest {
    private val mockZoneUseCase = mockk<ZoneUseCase>()
    private val zoneController = ZoneController(mockZoneUseCase)

    private val actorData = ActorData(
        actorId = ActorId("actor-1"),
        role = Role(RoleId("role-1"), PremisesId("prem-1"), Name("Role"), RoleType.USER),
        premisesId = PremisesId("prem-1"),
        principalType = ActorPrincipalType.USER,
        principal = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should create a zone`() {
        val request = ZoneRequest(name = "Living")
        val zone = Zone(
            zoneId = ZoneId("zone-0001"),
            premisesId = PremisesId("prem-1"),
            name = Name("Living"),
            createdBy = ActorId("actor-1"),
            createdAt = Instant.parse("2023-01-01T00:00:00Z"),
            version = null
        )

        every { mockZoneUseCase.createZone(any(), any()) } returns Mono.just(zone)

        val result = zoneController.createZone(request, actorData)

        assertNextWith(result) {
            it shouldBe ZoneResponse(
                zoneId = "zone-0001",
                premisesId = "prem-1",
                name = "Living",
                createdAt = Instant.parse("2023-01-01T00:00:00Z")
            )
            verify(exactly = 1) { mockZoneUseCase.createZone(match { it.name == Name("Living") }, actorData) }
        }
    }

    @Test
    fun `should get all zones`() {
        val zone1 = Zone(ZoneId("zone-0001"), PremisesId("prem-1"), Name("Living"), ActorId("actor-1"), Instant.parse("2023-01-01T00:00:00Z"))
        val zone2 = Zone(ZoneId("zone-0002"), PremisesId("prem-1"), Name("Kitchen"), ActorId("actor-1"), Instant.parse("2023-01-02T00:00:00Z"))
        every { mockZoneUseCase.getAllZones(any(), any()) } returns Flux.just(zone1, zone2)

        val resourcesData = ResourcesData(
            listOf(
                ResourceEntitlement(ResourceContext(ResourceType.ZONE, "zone-0001", ActionType.READ), PremisesId("prem-1")),
                ResourceEntitlement(ResourceContext(ResourceType.ZONE, "zone-0002", ActionType.READ), PremisesId("prem-1")),
            )
        )

        val flux = zoneController.getAllZones(actorData, resourcesData)

        val responses = flux.collectList().block()!!
        responses.size shouldBe 2
        responses[0] shouldBe ZoneResponse("zone-0001", "prem-1", "Living", Instant.parse("2023-01-01T00:00:00Z"))
        responses[1] shouldBe ZoneResponse("zone-0002", "prem-1", "Kitchen", Instant.parse("2023-01-02T00:00:00Z"))

        verify(exactly = 1) {
            mockZoneUseCase.getAllZones(actorData, match { list -> list.map { it.value } == listOf("zone-0001", "zone-0002") })
        }
    }
}

