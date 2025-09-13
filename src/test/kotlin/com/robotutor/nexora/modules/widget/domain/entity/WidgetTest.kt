package com.robotutor.nexora.modules.widget.domain.entity

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.modules.widget.domain.event.WidgetCreatedEvent
import com.robotutor.nexora.shared.domain.model.*
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant

class WidgetTest {
    @Test
    fun `create should initialize fields and emit WidgetCreatedEvent`() {
        val actor = ActorData(
            actorId = ActorId("actor-1"),
            role = Role(RoleId("role-1"), PremisesId("prem-1"), Name("Role"), RoleType.USER),
            premisesId = PremisesId("prem-1"),
            principalType = ActorPrincipalType.USER,
            principal = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
        )
        val cmd = CreateWidgetCommand(Name("Light"), FeedId("feed-1"), ZoneId("zone-1"), WidgetType.TOGGLE)

        val widgetId = WidgetId("widget-1")
        val widget = Widget.create(widgetId, cmd, actor)

        widget.widgetId shouldBe widgetId
        widget.premisesId shouldBe actor.premisesId
        widget.name shouldBe cmd.name
        widget.feedId shouldBe cmd.feedId
        widget.zoneId shouldBe cmd.zoneId
        widget.type shouldBe cmd.widgetType

        val events = widget.getDomainEvents()
        events.size shouldBe 1
        val event = events[0] as WidgetCreatedEvent
        event.widgetId shouldBe widgetId
    }
}

