package com.robotutor.nexora.modules.widget.interfaces.controller

import com.robotutor.nexora.modules.widget.application.WidgetUseCase
import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetId
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.modules.widget.interfaces.controller.dto.WidgetResponse
import com.robotutor.nexora.shared.domain.model.*
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import java.time.Instant

class WidgetControllerTest {
    private val mockWidgetUseCase = mockk<WidgetUseCase>()
    private val widgetController = WidgetController(mockWidgetUseCase)

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
    fun `should get widgets`() {
        val w1 = Widget(
            widgetId = WidgetId("widget-1"),
            premisesId = PremisesId("prem-1"),
            name = Name("Light"),
            feedId = FeedId("feed-1"),
            zoneId = ZoneId("zone-1"),
            type = WidgetType.TOGGLE,
            createdAt = Instant.parse("2023-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2023-01-01T00:00:00Z"),
        )
        val w2 = Widget(
            widgetId = WidgetId("widget-2"),
            premisesId = PremisesId("prem-1"),
            name = Name("Dimmer"),
            feedId = FeedId("feed-2"),
            zoneId = ZoneId("zone-2"),
            type = WidgetType.SLIDER,
            createdAt = Instant.parse("2023-01-02T00:00:00Z"),
            updatedAt = Instant.parse("2023-01-02T00:00:00Z"),
        )
        every { mockWidgetUseCase.getWidgets(any(), any()) } returns Flux.just(w1, w2)

        val resourcesData = ResourcesData(
            listOf(
                ResourceEntitlement(ResourceContext(ResourceType.WIDGET, "widget-1", ActionType.READ), PremisesId("prem-1")),
                ResourceEntitlement(ResourceContext(ResourceType.WIDGET, "widget-2", ActionType.READ), PremisesId("prem-1")),
            )
        )

        val resultFlux = widgetController.getWidgets(actorData, resourcesData)
        val responses = resultFlux.collectList().block()!!

        responses.size shouldBe 2
        responses[0] shouldBe WidgetResponse(
            widgetId = "widget-1",
            premisesId = "prem-1",
            name = "Light",
            feedId = "feed-1",
            type = WidgetType.TOGGLE,
            zoneId = "zone-1"
        )
        responses[1] shouldBe WidgetResponse(
            widgetId = "widget-2",
            premisesId = "prem-1",
            name = "Dimmer",
            feedId = "feed-2",
            type = WidgetType.SLIDER,
            zoneId = "zone-2"
        )

        verify(exactly = 1) {
            mockWidgetUseCase.getWidgets(actorData, match { ids -> ids.map { it.value } == listOf("widget-1", "widget-2") })
        }
    }
}

