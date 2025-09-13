package com.robotutor.nexora.modules.widget.application

import com.robotutor.nexora.modules.widget.application.command.CreateWidgetCommand
import com.robotutor.nexora.modules.widget.domain.entity.IdType
import com.robotutor.nexora.modules.widget.domain.entity.Widget
import com.robotutor.nexora.modules.widget.domain.entity.WidgetId
import com.robotutor.nexora.modules.widget.domain.entity.WidgetType
import com.robotutor.nexora.modules.widget.domain.event.WidgetEvent
import com.robotutor.nexora.modules.widget.domain.repository.WidgetRepository
import com.robotutor.nexora.shared.domain.event.EventPublisher
import com.robotutor.nexora.shared.domain.event.ResourceCreatedEvent
import com.robotutor.nexora.shared.domain.model.*
import com.robotutor.nexora.shared.domain.service.IdGeneratorService
import com.robotutor.nexora.testUtils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class WidgetUseCaseTest {
    private val widgetRepository = mockk<WidgetRepository>()
    private val idGeneratorService = mockk<IdGeneratorService>()
    private val widgetEventPublisher = mockk<EventPublisher<WidgetEvent>>()
    private val resourceEventPublisher = mockk<EventPublisher<ResourceCreatedEvent>>()

    private val widgetUseCase = WidgetUseCase(
        widgetRepository = widgetRepository,
        idGeneratorService = idGeneratorService,
        widgetEventPublisher = widgetEventPublisher,
        eventPublisher = resourceEventPublisher
    )

    @BeforeEach
    fun setup() {
        clearAllMocks()
        val fixedInstant = Instant.parse("2023-01-01T00:00:00Z")
        mockkStatic(Instant::class)
        every { Instant.now() } returns fixedInstant
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should create widget successfully`() {
        val actorData = ActorData(
            actorId = ActorId("actor-1"),
            role = Role(RoleId("role-1"), PremisesId("prem-1"), Name("Role"), RoleType.USER),
            premisesId = PremisesId("prem-1"),
            principalType = ActorPrincipalType.USER,
            principal = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
        )
        val command = CreateWidgetCommand(
            name = Name("Light Switch"),
            feedId = FeedId("feed-1"),
            zoneId = ZoneId("zone-1"),
            widgetType = WidgetType.TOGGLE
        )
        val generatedWidgetId = WidgetId("widget-000001")

        every { idGeneratorService.generateId(IdType.WIDGET_ID, WidgetId::class.java) } returns Mono.just(generatedWidgetId)
        every { widgetRepository.save(any()) } answers { Mono.just(firstArg()) }
        every { resourceEventPublisher.publish(any()) } returns Mono.just(Unit)
        every { widgetEventPublisher.publish(any()) } returns Mono.just(Unit)

        val result = widgetUseCase.createWidget(command, actorData)

        assertNextWith(result) {
            it.widgetId shouldBe generatedWidgetId
            it.premisesId shouldBe actorData.premisesId
            it.name shouldBe command.name
            it.feedId shouldBe command.feedId
            it.zoneId shouldBe command.zoneId
            it.type shouldBe command.widgetType
            it.createdAt shouldBe Instant.parse("2023-01-01T00:00:00Z")
            it.updatedAt shouldBe Instant.parse("2023-01-01T00:00:00Z")
        }

        verify(exactly = 1) {
            idGeneratorService.generateId(IdType.WIDGET_ID, WidgetId::class.java)
            widgetRepository.save(any())
            resourceEventPublisher.publish(any())
            widgetEventPublisher.publish(any())
        }
    }

    @Test
    fun `should get widgets for given premises and ids`() {
        val actorData = ActorData(
            actorId = ActorId("actor-1"),
            role = Role(RoleId("role-1"), PremisesId("prem-1"), Name("Role"), RoleType.USER),
            premisesId = PremisesId("prem-1"),
            principalType = ActorPrincipalType.USER,
            principal = UserData(UserId("user-1"), Name("John"), Email("john@example.com"), Instant.parse("2020-01-01T00:00:00Z"))
        )
        val widgetIds = listOf(WidgetId("widget-1"), WidgetId("widget-2"))

        val cmd1 = CreateWidgetCommand(Name("Widget1"), FeedId("feed-1"), ZoneId("zone-1"), WidgetType.TOGGLE)
        val cmd2 = CreateWidgetCommand(Name("Widget2"), FeedId("feed-2"), ZoneId("zone-2"), WidgetType.SLIDER)
        val w1 = Widget.create(WidgetId("widget-1"), cmd1, actorData)
        val w2 = Widget.create(WidgetId("widget-2"), cmd2, actorData)

        every { widgetRepository.findAllByPremisesIdAndWidgetIdIn(actorData.premisesId, widgetIds) } returns Flux.just(w1, w2)

        val flux = widgetUseCase.getWidgets(actorData, widgetIds)

        StepVerifier.create(flux)
            .expectNext(w1)
            .expectNext(w2)
            .verifyComplete()

        verify(exactly = 1) { widgetRepository.findAllByPremisesIdAndWidgetIdIn(actorData.premisesId, widgetIds) }
    }
}

